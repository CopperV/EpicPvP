package me.Vark123.EpicPvPArena.ArenaSystem.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;

public class PvPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("pvp"))
			return false;
		if(!(sender instanceof Player))
			return false;
		
		Player p = (Player) sender;
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresentOrElse(pp -> {
				int rank = DatabaseManager.getPlayerRanking(p);
				if(rank < 0) {
					p.sendMessage("§7["+Config.get().getPrefix()+"§7] "
							+ "§cBLAD! Nie moge pobrac Twojego miejsca rankingowego! Zglos to administratorowi!");
					return;
				}
				p.sendMessage("§7=============================");
				p.sendMessage("          "+Config.get().getPrefix()+"          ");
				p.sendMessage(" ");
				p.sendMessage("§4§l» §ePosiadasz §7§o"+pp.getPoints()+" §epunktow rankingowych");
				p.sendMessage("§4§l» §eZajmujesz §7§o"+rank+" miejsce §ew rankingu serwerowym");
				p.sendMessage("§4§l» §ePosiadasz §7§o"+pp.getTokens()+" §etokenow gladiatora");
				p.sendMessage("§4§l» §ePosiadasz §7§o"+String.format("%.2f", pp.getKD())+" §ewskaznik K/D");
				p.sendMessage("§4§l» §eRozegrales §7§o"+pp.getFights()+" §ewalk");
				p.sendMessage(" ");
				p.sendMessage("§7=============================");
			}, () -> p.sendMessage("§7["+Config.get().getPrefix()+"§7] "
					+ "§cBLAD! Nie ma Ciebie w rankingu! Zglos to administratorowi!"));
		return true;
	}

}
