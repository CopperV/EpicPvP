package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicPvPArena.ArenaSystem.FightStatus;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class PlayerFightQuitListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		check(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onKick(PlayerKickEvent e) {
		check(e.getPlayer());
	}
	
	private void check(Player p) {
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getFights().stream()
					.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
					.filter(fight -> fight.getStatus().equals(FightStatus.FIGHT)
							|| fight.getStatus().equals(FightStatus.TELEPORTATION)
							|| fight.getStatus().equals(FightStatus.PREPARING))
					.findAny()
					.ifPresent(fight -> {
						EpicPvPPlayer winner = fight.getPlayer1().equals(pp) ? fight.getPlayer2() : fight.getPlayer1();
						fight.finishCheatFight(winner);
					});
			});
	}
	
}
