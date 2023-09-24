package me.Vark123.EpicPvPArena.ArenaSystem.Commands;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.DatabaseManager;

public class RankingCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("ranking"))
			return false;
		
		int page = 0;
		if(args.length != 0) {
			String arg = args[0];
			if(StringUtils.isNumeric(arg)) {
				page = Integer.parseInt(arg) - 1;
				if(page < 0)
					page = 0;
			}
		}
		
		Map<String, Integer> ranking = DatabaseManager.getPvPRanking(page);
		if(ranking.isEmpty())
			return false;
		sender.sendMessage("§7["+Config.get().getPrefix()+"§7] §eRanking graczy PvP §7[§fStrona "+(page+1)+"§7]");
		MutableInt pos = new MutableInt(page*10 + 1);
		ranking.entrySet().stream()
			.forEach(entry -> {
				String nick = entry.getKey();
				int points = entry.getValue();
				sender.sendMessage("§4§l» §7"+pos.getAndIncrement()+". §e"+nick+" §7[§f"+points+"§7]");
			});
		return true;
	}

}
