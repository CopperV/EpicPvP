package me.Vark123.EpicPvPArena;

import org.bukkit.Bukkit;

import me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCommandManager;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.BaseArenaCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.PvPCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.RankingCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl.ArenaAcceptCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl.ArenaRejectCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl.ArenaSignCommand;
import me.Vark123.EpicPvPArena.ArenaSystem.Commands.ArenaCmdImpl.ArenaUnsignCommand;

public final class CommandManager {

	private CommandManager() { }
	
	public static void setExecutors() {
		Bukkit.getPluginCommand("pvp").setExecutor(new PvPCommand());
		Bukkit.getPluginCommand("ranking").setExecutor(new RankingCommand());
		Bukkit.getPluginCommand("arena").setExecutor(new BaseArenaCommand());
		
		ArenaCommandManager.get().registerSubcommand(new ArenaAcceptCommand());
		ArenaCommandManager.get().registerSubcommand(new ArenaRejectCommand());
		ArenaCommandManager.get().registerSubcommand(new ArenaSignCommand());
		ArenaCommandManager.get().registerSubcommand(new ArenaUnsignCommand());
	}
	
}
