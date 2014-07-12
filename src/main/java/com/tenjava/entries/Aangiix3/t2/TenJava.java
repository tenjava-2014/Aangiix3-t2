package com.tenjava.entries.Aangiix3.t2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class TenJava extends JavaPlugin implements Listener {
	private MySQL db;
	private final Map<String, String> requests = new HashMap<String, String>();
	private final Map<String, Long> timeouts = new HashMap<String, Long>();
	private final Map<String, Long> tagged = new HashMap<String, Long>();
	private final Map<String, Integer> backexp = new HashMap<String, Integer>();
	private final Map<String, ItemStack[]> backarmor = new HashMap<String, ItemStack[]>(), backinv = new HashMap<String, ItemStack[]>();
	private final List<Duel> runningduels = new ArrayList<Duel>();
	private ItemStack[] armorkit, invkit;
	private Location spawn1, spawn2;
	private String duelrequest, alreadyrequested, requestsent, requestaccepted, acceptedrequest, cannotuseincombat;
	private long timeout = 60000L, combattime = 6000L;
	private boolean ownstuff = false, useincombat = false;

	@Override
	public void onEnable() {
		final Server sr = this.getServer();
		saveDefaultConfig();
		loadConfig();
		sr.getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
		for (final Duel d : runningduels) {
			stopDuel(d);
		}
		tagged.clear();
		db.kill();
	}
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String args[]) {
		if (cmd.getName().equalsIgnoreCase("duel")) {
			if (sender.hasPermission("duel.admin") == false || sender instanceof Player == false) {
				sender.sendMessage("§3Duel with other players: §6right click them!");
				return true;
			} else if (args.length != 1) {
				showMenu(sender);
			} else if (args[0].equalsIgnoreCase("setspawn1")) {
				setSpawn((Player)sender, false);
			} else if (args[0].equalsIgnoreCase("setspawn2")) {
				setSpawn((Player)sender, true);
			} else if (args[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				loadConfig();
				sender.sendMessage("§3Config has been reloaded.");
				return true;
			} else {
				showMenu(sender);
			}
			return true;
		}
		return false;
	}
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
	}
	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		requests.remove(e.getPlayer().getName());
	}
	/*
	 * COMBAT CHECKS
	 */
	@EventHandler
	public void onDeath(final PlayerDeathEvent e) { // COMBAT CHECK
		if (useincombat) return;
		tagged.remove(e.getEntity().getName());
	}
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageByEntityEvent e) { // COMBAT CHECK
		if (useincombat) return;
		final Entity en = e.getEntity();
		if (en instanceof Player) {
			final Player p = (Player) en;
			final Entity en2 = e.getDamager();
			Player damager = null;
			if (en2 instanceof Player) {
				damager = (Player) en2;
			} else if (en2 instanceof Arrow && ((Arrow)en2).getShooter() instanceof Player) {
				damager = (Player) ((Arrow)en2).getShooter();
			} else if (en2 instanceof ThrownPotion && ((ThrownPotion)en2).getShooter() instanceof Player) {
				damager = (Player) ((ThrownPotion)en2).getShooter();
			}
			if (damager == null) return;
			tagged.put(damager.getName(), System.currentTimeMillis());
			tagged.put(p.getName(), System.currentTimeMillis());
		}
		return;
	}
	@EventHandler
	public void onEntityInteract(final PlayerInteractEntityEvent e) {
		final Entity en = e.getRightClicked();
		if (en instanceof Player) {
			final Player p = e.getPlayer(), p2 = (Player) en;
			final String pname = p.getName(), pname2 = p2.getName();
			String opponent = requests.get(pname), opponent2 = requests.get(pname2);
			if (!useincombat && tagged.containsKey(pname) && tagged.get(pname) + combattime >= System.currentTimeMillis()) {
				p.sendMessage(cannotuseincombat);
			} else if (pname == (opponent2 = opponent2 == null ? "" : opponent2)) { // Already requested
				p.sendMessage(alreadyrequested.replaceAll("%ply", pname2));
			} else if (pname2 == (opponent = opponent == null ? "" : opponent)) { // Accept request
				p.sendMessage(requestaccepted);
				p2.sendMessage(acceptedrequest.replaceAll("%ply", pname));
				requests.remove(pname);
				startDuel(p, p2);
			} else { // Send request
				p.sendMessage(requestsent);
				p2.sendMessage(duelrequest.replaceAll("%ply", pname));
				requests.put(pname2, pname);
				timeouts.put(pname2, System.currentTimeMillis() + timeout);
			}
			return;
		}
	}
	private void loadConfig() {
		final FileConfiguration config = this.getConfig();
		duelrequest = ChatColor.translateAlternateColorCodes('&', config.getString("messages.duelrequest"));
		requestsent = ChatColor.translateAlternateColorCodes('&', config.getString("messages.requestsent"));
		alreadyrequested = ChatColor.translateAlternateColorCodes('&', config.getString("messages.alreadyrequested"));
		requestaccepted = ChatColor.translateAlternateColorCodes('&', config.getString("messages.requestaccepted"));
		acceptedrequest = ChatColor.translateAlternateColorCodes('&', config.getString("messages.acceptedrequest"));
		cannotuseincombat = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cannotuseincombat"));
		timeout = config.getInt("settings.timeout") * 60000L;
		ownstuff = config.getBoolean("settings.ownstuff");
		useincombat = config.getBoolean("settings.useincombat");
		combattime = config.getInt("settings.combattime") * 1000L;
		armorkit = new ItemStack[4];
		short count = 3;
		for (final String s : Arrays.asList("kit.armor.helmet", "kit.armor.chestplate", "kit.armor.leggings", "kit.armor.boots")) {
			armorkit[count] = getItemStack(config.getString(s));
			count--;
		}
		invkit = new ItemStack[36];
		for (final String s : config.getStringList("kit.inventory")) {
			count++;
			invkit[count] = getItemStack(s);
		}
		if (db != null) db.kill();
		db = new MySQL(config.getString("mysql.url"), config.getString("mysql.username"), config.getString("mysql.password"));
		spawn1 = config.getVector("locations.spawn1.coords").toLocation(this.getServer().getWorld(config.getString("locations.spawn1.w")), (float) config.getDouble("locations.spawn1.yaw"), (float) config.getDouble("locations.spawn1.pitch"));
		spawn2 = config.getVector("locations.spawn2.coords").toLocation(this.getServer().getWorld(config.getString("locations.spawn2.w")), (float) config.getDouble("locations.spawn2.yaw"), (float) config.getDouble("locations.spawn2.pitch"));
	}
	private ItemStack getItemStack(final String s) {
		short value = 0;
		final String[] split1 = s.split(" ");
		String itemID = split1[0];
		if (split1[0].contains(":")) {
			final String[] split2 = split1[0].split(":");
			itemID = split2[0];
			value = Short.parseShort(split2[1]);
		}
		try {
			return new ItemStack(Integer.parseInt(itemID), Integer.parseInt(split1[1]), value);
		} catch (final Exception ex) {
			return new ItemStack(Material.AIR);
		}
	}
	private void startDuel(final Player p, final Player p2) {
		DuelData d = db.loadPlayer(p.getUniqueId()), d2 = db.loadPlayer(p2.getUniqueId());
		if (d == null) d = new DuelData(p.getUniqueId(), p.getName(), 0, 0, 0);
		if (d2 == null) d2 = new DuelData(p2.getUniqueId(), p.getName(), 0, 0, 0);
		p.teleport(spawn1);
		p.teleport(spawn2);
		// TODO: Vanish other players
		equipPlayer(p);
		equipPlayer(p2);
	}
	private void equipPlayer(final Player p) {
		p.closeInventory();
		if (ownstuff == false) {
			final PlayerInventory pi = p.getInventory();
			backinv.put(p.getName(), pi.getContents());
			backarmor.put(p.getName(), pi.getArmorContents());
			backexp.put(p.getName(), (int) p.getExp());
			pi.clear();
			pi.setArmorContents(null);
			p.setExp(0F);
			p.setLevel(0);
			pi.setArmorContents(armorkit);
			pi.setContents(invkit);
			p.updateInventory();
		}
		// Heal, feed and deactivate anything unfair
		for (final PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}
		p.setHealth(20D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setGameMode(GameMode.ADVENTURE);
		// TODO: Scoreboard
	}
	private void stopDuel(final Duel d) {
		return;
	}
	private void showMenu(final CommandSender sender) {
		sender.sendMessage(new String[] {
				"§7============= §3Duel §7============",
				"§cAdmin Commands:",
				"§3/duel setspawn1: §7Set Duel-Spawn 1",
				"§3/duel setspawn2: §7Set Duel-Spawn 2",
				"§3/duel reload: §7Reload config",
				"§7=============================="
		});
	}
	private void setSpawn(final Player p, final boolean first) {
		final FileConfiguration config = this.getConfig();
		if (first) {
			spawn1 = p.getLocation();
			config.set("locations.spawn1.w", spawn1.getWorld());
			config.set("locations.spawn1.coords", spawn1.toVector());
			config.set("locations.spawn1.yaw", spawn1.getYaw());
			config.set("locations.spawn1.pitch", spawn1.getPitch());
		} else {
			spawn2 = p.getLocation();
			config.set("locations.spawn2.w", spawn2.getWorld());
			config.set("locations.spawn2.coords", spawn2.toVector());
			config.set("locations.spawn2.yaw", spawn2.getYaw());
			config.set("locations.spawn2.pitch", spawn2.getPitch());
		}
		this.saveConfig();
		p.sendMessage("§3Spawn set.");
	}
}
