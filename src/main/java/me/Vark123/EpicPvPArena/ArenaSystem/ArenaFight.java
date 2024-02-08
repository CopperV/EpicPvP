package me.Vark123.EpicPvPArena.ArenaSystem;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
	
	private BossBar infobar;
	private BukkitTask teleportTask;
	private BukkitTask fightTask;
	
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
					startCountingDown();
				}

				p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f + timer * 0.1f);
				p2.playSound(p2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f + timer * 0.1f);
				p1.sendTitle("§e§lARENA PVP!", "§aTELEPORTACJA ZA "+timer, 5, 10, 15);
				p2.sendTitle("§e§lARENA PVP!", "§aTELEPORTACJA ZA "+timer, 5, 10, 15);
				--timer;
			}
		}.runTaskTimer(Main.getInst(), 0, 20*1);
	}
	
	public void startCountingDown() {
		Player p1 = player1.getPlayer();
		Player p2 = player2.getPlayer();
		status = FightStatus.PREPARING;

		infobar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
		infobar.setProgress(1);
		infobar.addPlayer(p1);
		infobar.addPlayer(p2);
		infobar.setVisible(true);
		new BukkitRunnable() {
			final double TIMER = Config.get().getCountingDownDuration();
			double timer = TIMER;
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
					startFight();
					return;
				}
				
				double progress = timer / TIMER;
				if(progress < 0) progress = 0;
				infobar.setProgress(progress);
				infobar.setTitle("§3Walka za: §o"+String.format("%.2f", timer)+" §3sekund");
				timer -= 0.05;
			}
		}.runTaskTimer(Main.getInst(), 0, 1);
	}
	
	public void startFight() {
		Player p1 = player1.getPlayer();
		Player p2 = player2.getPlayer();
		p1.playSound(p1, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1.2f);
		p2.playSound(p2, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1.2f);
		p1.sendTitle("§6§lWALKA", " ", 10, 10, 10);
		p2.sendTitle("§6§lWALKA", " ", 10, 10, 10);
		status = FightStatus.FIGHT;
		
		infobar.setTitle("");
		infobar.setProgress(1);
		
		fightTask = new BukkitRunnable() {
			final double TIMER = Config.get().getMaxFightTime();
			double timer = TIMER;
			@Override
			public void run() {
				if(isCancelled())
					return;
				
				if(timer <= 0) {
					cancel();
					
					double percent1 = p1.getHealth() / p1.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
					double percent2 = p2.getHealth() / p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
					EpicPvPPlayer winner = percent1 > percent2 ? player1 : player2;
					finishFight(winner);
					
					return;
				}
				
				double progress = timer / TIMER;
				if(progress < 0) progress = 0;
				infobar.setProgress(progress);
				infobar.setTitle("§3Pozostaly czas: §o"+((int) timer)+" §3sekund");
				timer -= 0.25;
			}
		}.runTaskTimer(Main.getInst(), 0, 5);
	}
	
	public void finishFight(EpicPvPPlayer winner) {
		EpicPvPPlayer loser = player1.equals(winner) ? player2: player1;
		int points = winner.getPoints() > loser.getPoints() ? minPoints : maxPoints;
		
		stopFight("§eWalka zakonczona! Wygral §7"+winner.getPlayer().getName());
		
		winner.addPoints(points);
		loser.removePoints(points);
		
		loser.incrementFight();
		winner.incrementFight();
		winner.incrementWins();
		
		winner.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eZdobyles w walce §7"+points+" §epunktow rankingowych");
		loser.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eStraciles w walce §7"+points+" §epunktow rankingowych");
	}
	
	public void finishCheatFight(EpicPvPPlayer winner) {
		EpicPvPPlayer loser = player1.equals(winner) ? player2: player1;
		int points = winner.getPoints() > loser.getPoints() ? minPoints : maxPoints;
		
		stopFight("§eWalka zakonczona! Wygral walkowerem §7"+winner.getPlayer().getName());
		
		winner.addPoints(points);
		loser.removePoints(points*2);
		
		loser.incrementFight();
		winner.incrementFight();
		winner.incrementWins();
		
		winner.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eZdobyles w walce §7"+points+" §epunktow rankingowych");
		if(loser.getPlayer().isOnline())
			loser.getPlayer().sendMessage("§7["+Config.get().getPrefix()+"§7] §eStraciles w walce §7"+(points*2)+" §epunktow rankingowych");
	}
	
	public void stopFight(String message) {
		if(teleportTask != null && !teleportTask.isCancelled())
			teleportTask.cancel();
		if(fightTask != null && !fightTask.isCancelled())
			fightTask.cancel();
		if(infobar != null) {
			infobar.setVisible(false);
			infobar.removeAll();
		}
		
		Player p1 = player1.getPlayer();
		Player p2 = player2.getPlayer();
		if(p1.isOnline())
			p1.sendMessage("§7["+Config.get().getPrefix()+"§7] §r"+message);
		if(p2.isOnline())
			p2.sendMessage("§7["+Config.get().getPrefix()+"§7] §r"+message);
		
		status = FightStatus.END;
		PvPArenaManager.get().getFights().remove(this);
		
		arena.getResp1().toBukkitLocation().getWorld().getPlayers()
			.forEach(p -> {
				p.setLastDamageCause(null);
				p.setHealth(0);
			});
	}
	
}
