package com.tenjava.entries.Aangiix3.t2;

import java.util.HashMap;
import java.util.Map;

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
	private String duelrequest = "%ply has sent you a duel request. Accept it by right clicking him.";
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
			final Player p = (Player) en;
			if (requests.containsKey(p.getName())) {
				// TODO
			} else {
				p.sendMessage(duelrequest);
				requests.put(p.getName(), e.getPlayer().getName());
			}

		}
	}
	private void loadConfig() {
		final FileConfiguration config = this.getConfig();
		duelrequest = config.getString("duelrequest");
		timeout = config.getInt("timeout") * 1000L;
	}
}
