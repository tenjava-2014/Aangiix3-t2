package com.tenjava.entries.Aangiix3.t2;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin implements Listener {
	final Map<String, String> requests = new HashMap<String, String>();
	private String duelrequest, alreadyrequested, requestsent, requestaccepted, acceptedrequest;
	private long timeout = 60000L;

	@Override
	public void onEnable() {
		final Server sr = this.getServer();
		saveDefaultConfig();
		loadConfig();
		sr.getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
	}
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
	}
	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		requests.remove(e.getPlayer().getName());
	}
	@EventHandler
	public void onEntityInteract(final PlayerInteractEntityEvent e) {
		final Entity en = e.getRightClicked();
		if (en instanceof Player) {
			final Player p = e.getPlayer(), p2 = (Player) en;
			final String pname = p.getName(), pname2 = p2.getName();
			String opponent = requests.get(pname2);
			if (pname == (opponent = opponent == null ? "" : opponent)) { // Already clicked
				p.sendMessage(alreadyrequested.replaceAll("%ply", pname2));
				return;
			} else if (requests.containsValue(pname2) && opponent == pname) { // Already requested
				p.sendMessage(requestaccepted);
				p2.sendMessage(acceptedrequest.replaceAll("%ply", pname));
				requests.remove(pname);
			} else {
				p.sendMessage(requestsent);
				p2.sendMessage(duelrequest.replaceAll("%ply", pname));
				requests.put(pname2, pname);
			}
		}
	}
	private void loadConfig() {
		final FileConfiguration config = this.getConfig();
		duelrequest = ChatColor.translateAlternateColorCodes('&', config.getString("messages.duelrequest"));
		requestsent = ChatColor.translateAlternateColorCodes('&', config.getString("messages.requestsent"));
		alreadyrequested = ChatColor.translateAlternateColorCodes('&', config.getString("messages.alreadyrequested"));
		requestaccepted = ChatColor.translateAlternateColorCodes('&', config.getString("messages.requestaccepted"));
		acceptedrequest = ChatColor.translateAlternateColorCodes('&', config.getString("messages.acceptedrequest"));
		timeout = config.getInt("settings.timeout") * 1000L;
	}
}
