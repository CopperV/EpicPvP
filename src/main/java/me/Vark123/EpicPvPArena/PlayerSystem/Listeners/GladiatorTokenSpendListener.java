package me.Vark123.EpicPvPArena.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.Vark123.EpicRPG.AdvancedBuySystem.AdvancedBuyEvent;

public class GladiatorTokenSpendListener implements Listener {
	
	@EventHandler
	public void onBuy(AdvancedBuyEvent e) {
		if(e.isCancelled())
			return;
		
		if(!e.getConditions().containsKey("token"))
			return;
		
		int tokens = Integer.parseInt(e.getConditions().get("token"));
		
		Player p = e.getPlayer();
		PvPPlayerManager.get().getPvPPlayer(p).ifPresent(pp -> {
			if(!pp.hasEnoughTokens(tokens)) {
				e.setCancelled(true);
				return;
			}
			
			e.addBuyAction(_p -> pp.removeTokens(tokens));
		});
	}

}
