package me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Player;

import me.Vark123.EpicPvPArena.ArenaSystem.ArenaFight;
import me.Vark123.EpicPvPArena.ArenaSystem.FightConfirmation;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.AArenaCommand;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class ArenaAcceptCommand extends AArenaCommand {

	public ArenaAcceptCommand() {
		super("accept", new String[]{"akceptuj"});
	}

	@Override
	public boolean canUse(Player sender) {
		MutableBoolean result = new MutableBoolean();
		PvPPlayerManager.get().getPvPPlayer(sender)
			.ifPresent(pp -> {
				if(PvPArenaManager.get().getRecordedPlayers().contains(pp))
					return;
				if(PvPArenaManager.get().getConfirmations().stream()
						.map(confirm -> confirm.getFight())
						.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
						.findAny()
						.isEmpty())
					return;
				if(PvPArenaManager.get().getFights().stream()
						.filter(fight -> fight.getPlayer1().equals(pp) || fight.getPlayer2().equals(pp))
						.findAny()
						.isPresent())
					return;
				FightConfirmation confirm = PvPArenaManager.get().getConfirmations().stream()
						.filter(conf -> conf.getFight().getPlayer1().equals(pp) || conf.getFight().getPlayer2().equals(pp))
						.findAny()
						.get();
				ArenaFight fight = confirm.getFight();
				if(fight.getPlayer1().equals(pp) && confirm.isPlayer1Acceptance())
					return;
				if(fight.getPlayer2().equals(pp) && confirm.isPlayer2Acceptance())
					return;
				result.setTrue();
			});
		return result.booleanValue();
	}

	@Override
	public boolean useCommand(Player sender, String... args) {
		EpicPvPPlayer pp = PvPPlayerManager.get().getPvPPlayer(sender).get();
		PvPArenaManager.get().acceptFight(pp);
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §e/arena akceptuj §7- Zaakceptuj wylosowana walke na arenie PvP");
		
	}

}
