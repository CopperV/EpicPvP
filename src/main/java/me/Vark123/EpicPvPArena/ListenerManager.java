package me.Vark123.EpicPvPArena;

import org.bukkit.Bukkit;

import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.FightDeathListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.PlayerConfirmationQuitListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.PlayerFightQuitListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.WorldChangeListener;
import me.Vark123.EpicPvPArena.PlayerSystem.Listeners.PlayerJoinListener;
import me.Vark123.EpicPvPArena.PlayerSystem.Listeners.PlayerQuitListener;

public final class ListenerManager {

	private ListenerManager() { }
	
	public static void registerListeners() {
		Main inst = Main.getInst();
		
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), inst);

		Bukkit.getPluginManager().registerEvents(new FightDeathListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerFightQuitListener(), inst);
		Bukkit.getPluginManager().registerEvents(new WorldChangeListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerConfirmationQuitListener(), inst);
	}
	
}
