package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.Vark123.EpicPvPArena.ArenaSystem.FightStatus;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class FightDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getFights().stream()
					.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
					.filter(fight -> fight.getStatus().equals(FightStatus.FIGHT))
					.findAny()
					.ifPresent(fight -> {
						EpicPvPPlayer winner = fight.getPlayer1().equals(pp) ? fight.getPlayer2() : fight.getPlayer1();
						fight.finishFight(winner);
					});
			});
	}
	
}
