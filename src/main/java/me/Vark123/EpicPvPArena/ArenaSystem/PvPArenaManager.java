package me.Vark123.EpicPvPArena.ArenaSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public final class PvPArenaManager {

	private static final PvPArenaManager inst = new PvPArenaManager();
	
	private final Collection<ArenaTier> tiers;
	private final Collection<PvPArena> arenas;
	
	private PvPArenaManager() {
		tiers = new ArrayList<>();
		arenas = new ArrayList<>();
	}
	
	public static final PvPArenaManager get() {
		return inst;
	}
	
	public void registerTier(ArenaTier tier) {
		tiers.add(tier);
	}
	
	public void registerArena(PvPArena arena) {
		arenas.add(arena);
	}
	
	public Optional<ArenaTier> getPlayerTier(Player p) {
		return tiers.stream()
				.filter(tier -> tier.isPlayerInTier(p))
				.findAny();
	}
	
}
