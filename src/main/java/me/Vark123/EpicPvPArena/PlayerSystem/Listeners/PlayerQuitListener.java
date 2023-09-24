package me.Vark123.EpicPvPArena.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		playerCleaner(e.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerQuitEvent e) {
		playerCleaner(e.getPlayer());
	}
	
	private void playerCleaner(Player p) {
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				DatabaseManager.savePlayer(pp);
				PvPPlayerManager.get().unregisterPlayer(pp);
			});
	}
	
}
