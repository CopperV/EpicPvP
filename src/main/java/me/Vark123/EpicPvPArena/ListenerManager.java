package me.Vark123.EpicPvPArena;

import org.bukkit.Bukkit;

import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.ArenaSummaryListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.FightDeathListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.PlayerConfirmationQuitListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.PlayerFightQuitListener;
import me.Vark123.EpicPvPArena.ArenaSystem.Listeners.WorldChangeListener;
import me.Vark123.EpicPvPArena.PlayerSystem.Listeners.PlayerJoinListener;
import me.Vark123.EpicPvPArena.PlayerSystem.Listeners.PlayerQuitListener;
import me.nikl.calendarevents.CalendarEventsApi;

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

		Bukkit.getPluginManager().registerEvents(new ArenaSummaryListener(), inst);
	
		CalendarEventsApi calendar = inst.getCalendar();
		if(calendar.isRegisteredEvent("reset_pvp_ranking"))
			calendar.removeEvent("reset_pvp_ranking");
		calendar.addEvent("reset_pvp_ranking", "monday", "10:35");
	}
	
}
