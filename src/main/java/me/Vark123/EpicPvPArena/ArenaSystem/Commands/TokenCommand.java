package me.Vark123.EpicPvPArena.ArenaSystem.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;

import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.Vark123.EpicRPG.Main;

public class TokenCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("token"))
			return false;
		if(!sender.hasPermission("epicrpg.tokens")) {
			sender.sendMessage(Main.getInstance().getPrefix()+" §cNie posiadasz uprawnien do tej komendy!");
			return false;
		}
		if(args.length<3) {
			showCorrectUsage(sender);
			return false;
		}
		if(Bukkit.getPlayerExact(args[1])==null) {
			sender.sendMessage(Main.getInstance().getPrefix()+" §cGracz §a§o"+args[0]+" §cjest offline");
			return false;
		}
		if(!StringUtils.isNumeric(args[2])){
			sender.sendMessage(Main.getInstance().getPrefix()+args[2]+" §cnie jest liczba");
			return false;
		}
		

		Player p = Bukkit.getPlayer(args[1]);
		int amount = Integer.parseInt(args[2]);
		PvPPlayerManager.get().getPvPPlayer(p)
			.ifPresent(pp -> {
				switch(args[0].toLowerCase()) {
					case "add":
						pp.addTokens(amount);
						p.sendMessage(Main.getInstance().getPrefix()+" §aOtrzymales §4§o"+amount+" Tokenow Gladiatora");
						break;
					case "remove":
						pp.removeTokens(amount);
						p.sendMessage(Main.getInstance().getPrefix()+" §aOdebrano Ci §4§o"+amount+" Tokenow Gladiatora");
						break;
				}
			});
		return true;
	}
	
	private void showCorrectUsage(CommandSender sender) {
		sender.sendMessage("§7["+Config.get().getPrefix()+"§7] §ePoprawne uzycie komendy §e§o/token:");
		sender.sendMessage("§4- §c/token add/remove <player> <amount>");
	}

}
