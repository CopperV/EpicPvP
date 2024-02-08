package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.Vark123.EpicPvPArena.ArenaSystem.FightStatus;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class ArenaPreparingListeners implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.isCancelled())
			return;
		
		Entity entity = e.getEntity();
		if(!(entity instanceof Player))
			return;
		
		Player p = (Player) entity;
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getFights().stream()
				.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
					.filter(fight -> fight.getStatus().equals(FightStatus.PREPARING))
					.findAny()
					.ifPresent(fight -> e.setCancelled(true));
			});
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		if(e.getFrom().getBlock().getLocation().equals(
				e.getTo().getBlock().getLocation()))
			return;
		
		
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				PvPArenaManager.get().getFights().stream()
				.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
					.filter(fight -> fight.getStatus().equals(FightStatus.PREPARING))
					.findAny()
					.ifPresent(fight -> e.setCancelled(true));
			});
	}
	
}
