package me.Vark123.EpicPvPArena.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicPvPArena.ArenaSystem.FightStatus;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.Vark123.EpicRPG.HealthSystem.RpgPlayerHealEvent;
import me.Vark123.EpicRPG.Potions.RpgPlayerManaRegenEvent;

public class FightPotionDebuffListener implements Listener {

	@EventHandler
	public void onHeal(RpgPlayerHealEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = e.getP();
		PvPPlayerManager.get().getPvPPlayer(p).ifPresent(pvp -> {
			PvPArenaManager.get().getFights().parallelStream()
				.filter(fight -> fight.getStatus().equals(FightStatus.FIGHT))
				.filter(fight -> fight.getPlayer1().equals(pvp) 
						|| fight.getPlayer2().equals(pvp))
				.findAny()
				.ifPresent(fight -> e.setHeal(e.getHealAmount()*0.5));
		});
	}
	
	@EventHandler
	public void onManaRegen(RpgPlayerManaRegenEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		PvPPlayerManager.get().getPvPPlayer(p).ifPresent(pvp -> {
			PvPArenaManager.get().getFights().parallelStream()
				.filter(fight -> fight.getStatus().equals(FightStatus.FIGHT))
				.filter(fight -> fight.getPlayer1().equals(pvp) 
						|| fight.getPlayer2().equals(pvp))
				.findAny()
				.ifPresent(fight -> e.setRegen((int) (e.getRegen()*0.5)));
		});
	}
	
}
