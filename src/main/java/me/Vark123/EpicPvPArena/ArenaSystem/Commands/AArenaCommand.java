package me.Vark123.EpicPvPArena.ArenaSystem.Commands;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AArenaCommand {
	protected String cmd;
	protected String[] aliases;
	
	public abstract boolean canUse(Player sender);
	public abstract boolean useCommand(Player sender, String... args);
	public abstract void showCorrectUsage(Player sender);
}
