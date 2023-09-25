package me.Vark123.EpicPvPArena.PlayerSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.Main;

@Getter
public final class PvPPlayerManager {

	private static final PvPPlayerManager inst = new PvPPlayerManager();
	
	private Collection<EpicPvPPlayer> players;
	
	private BukkitTask saverTask;
	
	private PvPPlayerManager() {
		players = new HashSet<>();
		
		saverTask = new BukkitRunnable() {
			@Override
			public void run() {
				players.forEach(DatabaseManager::savePlayer);
			}
		}.runTaskTimer(Main.getInst(), 0, 20*Config.get().getPlayerSaveInterval());
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
