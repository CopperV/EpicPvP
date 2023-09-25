package me.Vark123.EpicPvPArena;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPPlaceholders;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.nikl.calendarevents.CalendarEvents;
import me.nikl.calendarevents.CalendarEventsApi;

@Getter
public class Main extends JavaPlugin {

	@Getter
	private static Main inst;

	private CalendarEventsApi calendar;

	@Override
	public void onEnable() {
		inst = this;

		CalendarEvents calend = (CalendarEvents) Bukkit.getPluginManager().getPlugin("CalendarEvents");
		calendar = calend.getApi();
		
		CommandManager.setExecutors();
		ListenerManager.registerListeners();
		FileManager.init();
		DatabaseManager.init();
		
		Bukkit.getOnlinePlayers().stream()
			.map(DatabaseManager::loadPlayer)
			.forEach(PvPPlayerManager.get()::registerPlayer);
		
		new PvPPlaceholders().register();
	}

	@Override
	public void onDisable() {
		PvPArenaManager.get().getConfirmations().clear();
		PvPArenaManager.get().getFights().forEach(fight -> fight.stopFight("§cWylaczenie pluginu na Areny PvP"));
		PvPArenaManager.get().getArenaParser().cancel();
		
		PvPPlayerManager.get().getPlayers().forEach(DatabaseManager::savePlayer);
		PvPPlayerManager.get().getPlayers().clear();
		PvPPlayerManager.get().getSaverTask().cancel();
		
		DatabaseManager.close();
	}
	
}
