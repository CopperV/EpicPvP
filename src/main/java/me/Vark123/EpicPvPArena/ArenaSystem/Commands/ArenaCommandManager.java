package me.Vark123.EpicPvPArena.ArenaSystem.Commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class ArenaCommandManager {

	private static final ArenaCommandManager inst = new ArenaCommandManager();
	
	private final Map<String, AArenaCommand> arenaSubcommands;
	
	private ArenaCommandManager() {
		arenaSubcommands = new LinkedHashMap<>();
	}
	
	public static final ArenaCommandManager get() {
		return inst;
	}
	
	public void registerSubcommand(AArenaCommand subcmd) {
		arenaSubcommands.put(subcmd.getCmd().toLowerCase(), subcmd);
		for(String alias : subcmd.getAliases())
			arenaSubcommands.put(alias, subcmd);
	}
	
	public Optional<AArenaCommand> getClanSubcommand(String subcmd) {
		return Optional.ofNullable(arenaSubcommands.get(subcmd.toLowerCase()));
	}
	
}
