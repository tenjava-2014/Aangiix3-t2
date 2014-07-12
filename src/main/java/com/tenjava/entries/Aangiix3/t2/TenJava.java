package com.tenjava.entries.Aangiix3.t2;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		final Server sr = this.getServer();
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

	}
	@EventHandler
	public void onEntityInteract(final PlayerInteractEntityEvent e) {

	}
}
