package me.Vark123.EpicPvPArena.ArenaSystem.Listeners;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.FileManager;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.nikl.calendarevents.CalendarEvent;

public class ArenaSummaryListener implements Listener {

	@EventHandler
	public void onReset(CalendarEvent e) {
		if(e.isCancelled())
			return;
		if(!e.getLabels().contains("reset_pvp_ranking"))
			return;
		
		LocalDate date = LocalDate.now();
		int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
		int year = date.getYear();
		Bukkit.broadcastMessage("§7["+Config.get().getPrefix()+"§7] "
				+ "§eSezon rankingowy §7§o"+year+"-"+week+" §ezakonczyl sie. Wszystkie walki zostaja przerwane i rozdane tokeny");
		
		PvPArenaManager.get().getFights().forEach(fight -> fight.stopFight("§eWalka przerwana z powodu podsumowania sezonu rankingowego"));
		Bukkit.getOnlinePlayers().stream()
			.map(PvPPlayerManager.get()::getPvPPlayer)
			.filter(pp -> pp.isPresent())
			.map(pp -> pp.get())
			.forEach(DatabaseManager::savePlayer);
		FileManager.archiveRanking(year+"-"+week);
		DatabaseManager.resetRanking();
	}
	
}
