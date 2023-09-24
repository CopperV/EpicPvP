package me.Vark123.EpicPvPArena.PlayerSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public final class PvPPlayerManager {

	private static final PvPPlayerManager inst = new PvPPlayerManager();
	
	private Collection<EpicPvPPlayer> players;
	
	private PvPPlayerManager() {
		players = new HashSet<>();
	}
	
	public static final PvPPlayerManager get() {
		return inst;
	}
	
	public void registerPlayer(EpicPvPPlayer p) {
		players.add(p);
	}
	
	public void unregisterPlayer(EpicPvPPlayer p) {
		players.remove(p);
	}
	
	public Optional<EpicPvPPlayer> getPvPPlayer(Player p) {
		return players.stream()
				.filter(pvp -> pvp.getPlayer().equals(p))
				.findAny();
	}
	
}
