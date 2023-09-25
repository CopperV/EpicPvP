package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import me.Vark123.EpicPvPArena.ArenaSystem.FightStatus;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class WorldChangeListener implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getFights().stream()
					.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
					.filter(fight -> fight.getStatus().equals(FightStatus.FIGHT))
					.findAny()
					.ifPresent(fight -> {
						EpicPvPPlayer winner = fight.getPlayer1().equals(pp) ? fight.getPlayer2() : fight.getPlayer1();
						fight.finishCheatFight(winner);
					});
			});
	}
	
}
