package me.Vark123.EpicPvPArena.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		EpicPvPPlayer pp = DatabaseManager.loadPlayer(p);
		PvPPlayerManager.get().registerPlayer(pp);
	}
	
}
