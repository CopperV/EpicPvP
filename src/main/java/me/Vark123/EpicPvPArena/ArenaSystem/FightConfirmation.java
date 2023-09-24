package me.Vark123.EpicPvPArena.ArenaSystem;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.Main;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;

@Getter
public class FightConfirmation {

	private static final String expMsg = "§7["+Config.get().getPrefix()+"§7] §eCzas na zaakceptowanie wyzwania sie skonczyl. Walka sie nie odbedzie!";
	private static final String noAcceptMsg = "§7["+Config.get().getPrefix()+"§7] §ePrzeciwnik nie zaakceptowal wyzwania na czas. Walka sie nie odbedzie!";
	private static final String rejectInfoMsg = "§7["+Config.get().getPrefix()+"§7] §ePrzeciwnik odrzucil wyzwanie z Toba. Twoja walka sie nie rozpocznie!";
	private static final String rejectMsg = "§7["+Config.get().getPrefix()+"§7] §eOdrzuciles wyzwanie na arenie!";
	private static final String acceptInfoMsg = "§7["+Config.get().getPrefix()+"§7] §ePrzeciwnik zaakceptowal wyzwanie walki z Toba!";
	private static final String acceptMsg = "§7["+Config.get().getPrefix()+"§7] §ePrzyjales wyzwanie na walne na arenie!";
	
	private ArenaFight fight;
	
	@Setter
	private boolean player1Acceptance = false;
	@Setter
	private boolean player2Acceptance = false;
	
	private BukkitTask confirmationTask;
	
	public FightConfirmation(ArenaFight fight) {
		super();
		this.fight = fight;
		
		FightConfirmation confirm = this;
		confirmationTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(isCancelled())
					return;
				Player p1 = fight.getPlayer1().getPlayer();
				Player p2 = fight.getPlayer2().getPlayer();
				if(p1.isOnline()) {
					String msg = player1Acceptance ? noAcceptMsg : expMsg;
					p1.sendMessage(msg);
				}
				if(p2.isOnline()) {
					String msg = player2Acceptance ? noAcceptMsg : expMsg;
					p2.sendMessage(msg);
				}
				
				PvPArenaManager.get().getConfirmations().remove(confirm);
			}
		}.runTaskLaterAsynchronously(Main.getInst(), 20*Config.get().getAcceptanceDuration());
	}
	
	public void acceptFight(EpicPvPPlayer pp) {
		EpicPvPPlayer opponent = fight.getPlayer1().equals(pp) ? fight.getPlayer2() : fight.getPlayer1();
		Player p1 = pp.getPlayer();
		Player p2 = opponent.getPlayer();
		
		if(fight.getPlayer1().equals(pp))
			player1Acceptance = true;
		else
			player2Acceptance = true;
		
		p1.sendMessage(acceptMsg);
		p2.sendMessage(acceptInfoMsg);
		
		if(!player1Acceptance || !player2Acceptance)
			return;
		
		confirmationTask.cancel();
		PvPArenaManager.get().getConfirmations().remove(this);
		PvPArenaManager.get().startFight(fight);
	}
	
	public void rejectFight(EpicPvPPlayer pp) {
		EpicPvPPlayer opponent = fight.getPlayer1().equals(pp) ? fight.getPlayer2() : fight.getPlayer1();
		Player p1 = pp.getPlayer();
		Player p2 = opponent.getPlayer();

		p1.sendMessage(rejectMsg);
		p2.sendMessage(rejectInfoMsg);
		
		confirmationTask.cancel();
		PvPArenaManager.get().getConfirmations().remove(this);
	}
	
}
