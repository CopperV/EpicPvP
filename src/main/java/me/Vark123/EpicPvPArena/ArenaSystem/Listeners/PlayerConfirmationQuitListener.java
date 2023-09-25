package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class PlayerConfirmationQuitListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(PlayerQuitEvent e) {
		check(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onKick(PlayerKickEvent e) {
		check(e.getPlayer());
	}
	
	private void check(Player p) {
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getConfirmations().stream()
					.filter(confirm -> confirm.getFight().getPlayer1().equals(pp) || confirm.getFight().getPlayer2().equals(pp))
					.findAny()
					.ifPresent(confirm -> {
						confirm.rejectFight(pp);
					});
			});
	}
	
}
