package me.Vark123.EpicPvPArena.ArenaSystem;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.Main;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;

@Getter
@Builder
@AllArgsConstructor
public class ArenaFight {

	private PvPArena arena;
	@Setter
	private FightStatus status;
	
	private EpicPvPPlayer player1;
	private EpicPvPPlayer player2;
	
	private int minPoints;
	private int maxPoints;
	
	private BukkitTask teleportTask;
	
	public void calcPoints() {
		int basePointsMod = Config.get().getBasePointsMod();
		int diff = Math.abs(player1.getPoints() - player2.getPoints());
		int scale = Config.get().getPointDiffLimit()/10;
		int mod = diff/scale;
		
		minPoints = basePointsMod - mod;
		maxPoints = basePointsMod + mod;
	}
	
	public void startTeleportation() {
		Player p1 = player1.getPlayer();
		Player p2 = player2.getPlayer();
		
		teleportTask = new BukkitRunnable() {
			int timer = 5;
			@Override
			public void run() {
				if(isCancelled())
					return;
				
				if(!p1.isOnline() || !p2.isOnline()) {
					this.cancel();
					stopFight("§ePrzeciwnik wyszedl z gry. Walka zostaje przerwana");
					return;
				}
				
				if(timer <= 0) {
					cancel();
					p1.teleport(arena.getResp1().toBukkitLocation());
					p2.teleport(arena.getResp2().toBukkitLocation());
					p1.playSound(p1.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0.8f);
					p2.playSound(p1.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 0.8f);
					status = FightStatus.FIGHT;
				}

				p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f + timer * 0.1f);
				p2.playSound(p2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f + timer * 0.1f);
				p1.sendTitle("§e§lARENA PVP!", "§aTELEPORTACJA ZA "+timer, 5, 10, 15);
				p2.sendTitle("§e§lARENA PVP!", "§aTELEPORTACJA ZA "+timer, 5, 10, 15);
				--timer;
			}
		}.runTaskTimer(Main.getInst(), 0, 20*1);
	}
	
	public void finishFight(EpicPvPPlayer winner) {
		EpicPvPPlayer loser = player1.equals(winner) ? player2: player1;
		int points = winner.getPoints() > loser.getPoints() ? minPoints : maxPoints;
		
		stopFight("§eWalka zakonczona! Wygral §7"+winner.getPlayer().getName());
		
		winner.addPoints(points);
		loser.removePoints(points);
		
		winner.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eZdobyles w walce §7"+points+" §epunktow rankingowych");
		loser.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eStraciles w walce §7"+points+" §epunktow rankingowych");
	}
	
	public void finishCheatFight(EpicPvPPlayer winner) {
		EpicPvPPlayer loser = player1.equals(winner) ? player2: player1;
		int points = winner.getPoints() > loser.getPoints() ? minPoints : maxPoints;
		
		stopFight("§eWalka zakonczona! Wygral walkowerem §7"+winner.getPlayer().getName());
		
		winner.addPoints(points);
		loser.removePoints(points*2);
		
		winner.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eZdobyles w walce §7"+points+" §epunktow rankingowych");
		if(loser.getPlayer().isOnline())
			loser.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eStraciles w walce §7"+(points*2)+" §epunktow rankingowych");
	}
	
	public void stopFight(String message) {
		if(teleportTask != null && !teleportTask.isCancelled())
			teleportTask.cancel();
		
		Player p1 = player1.getPlayer();
		Player p2 = player2.getPlayer();
		if(p1.isOnline())
			p1.sendMessage("§7["+Config.get().getPrefix()+"§7] §r"+message);
		if(p2.isOnline())
			p2.sendMessage("§7["+Config.get().getPrefix()+"§7] §r"+message);
		
		status = FightStatus.END;
		PvPArenaManager.get().getFights().remove(this);
		
		arena.getResp1().toBukkitLocation().getWorld().getPlayers()
			.forEach(p -> p.setHealth(0));
	}
	
}
