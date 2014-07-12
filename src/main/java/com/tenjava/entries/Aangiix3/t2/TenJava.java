package com.tenjava.entries.Aangiix3.t2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
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
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin implements Listener {
	private MySQL db;
	private final Map<String, String> requests = new HashMap<String, String>();
	private final Map<String, Long> timeouts = new HashMap<String, Long>();
	private final Map<String, Long> tagged = new HashMap<String, Long>();
	private ItemStack[] armorkit, invkit;
	private String duelrequest, alreadyrequested, requestsent, requestaccepted, acceptedrequest;
	private long timeout = 60000L;
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
		tagged.clear();
		db.kill();
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
			if (pname == (opponent2 = opponent2 == null ? "" : opponent2)) { // Already requested
				p.sendMessage(alreadyrequested.replaceAll("%ply", pname2));
			} else if (pname2 == (opponent = opponent == null ? "" : opponent)) { // Accept request
				p.sendMessage(requestaccepted);
				p2.sendMessage(acceptedrequest.replaceAll("%ply", pname));
				requests.remove(pname);
				DuelData d = db.loadPlayer(p.getUniqueId()), d2 = db.loadPlayer(p2.getUniqueId());
				if (d == null) d = new DuelData(p.getUniqueId(), p.getName(), 0, 0, 0);
				if (d2 == null) d2 = new DuelData(p2.getUniqueId(), p.getName(), 0, 0, 0);
				startGame(d, d2);
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
		timeout = config.getInt("settings.timeout") * 60000L;
		ownstuff = config.getBoolean("settings.ownstuff");
		useincombat = config.getBoolean("settings.useincombat");
		armorkit = new ItemStack[4];
		short count = 0;
		for (final String s : Arrays.asList("kit.armor.helmet", "kit.armor.chestplate", "kit.armor.leggings", "kit.armor.boots")) {
			armorkit[count] = getItemStack(config.getString(s));
			count++;
		}
		count = 0;
		for (final String s : config.getStringList("kit.inventory")) {
			invkit[count] = getItemStack(s);
		}
		db = new MySQL(config.getString("mysql.url"), config.getString("mysql.username"), config.getString("mysql.password"));
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
			return new ItemStack(Integer.parseInt(itemID), Integer.parseInt(split1[1], value));
		} catch (final Exception ex) {
			return new ItemStack(Material.AIR);
		}
	}
	private void startGame(final DuelData d, final DuelData d2) {

	}
}
