package me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Player;

import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.AArenaCommand;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class ArenaRejectCommand extends AArenaCommand {

	public ArenaRejectCommand() {
		super("reject", new String[]{"odrzuc"});
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
				result.setTrue();
			});
		return result.booleanValue();
	}

	@Override
	public boolean useCommand(Player sender, String... args) {
		EpicPvPPlayer pp = PvPPlayerManager.get().getPvPPlayer(sender).get();
		PvPArenaManager.get().rejectFight(pp);
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §e/arena odrzuc §7- Odrzuc wylosowana walke na arenie PvP");
		
	}

}
