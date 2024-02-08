package me.Vark123.EpicPvPArena.ArenaSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import me.Vark123.EpicPvPArena.DatabaseManager;
import me.Vark123.EpicPvPArena.FileManager;
import me.Vark123.EpicPvPArena.Tools.Pair;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PvPPlaceholders extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
			return "Vark123";
	}

	@Override
	public String getIdentifier() {
		return "epicpvp";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String identifier) {
		if(identifier.contains("toppresent_")) {
			String strTop = identifier.split("_")[1];
			if(!StringUtils.isNumeric(strTop))
				return "";
			int top = Integer.parseInt(strTop);
			Pair<String, Integer> result = DatabaseManager.getPlayerAtRank(top, 900, 10);
			if(result == null)
				return "";
			return result.getKey()+"    §7§o"+result.getValue();
		}
		if(identifier.contains("topold_")) {
			String strTop = identifier.split("_")[1];
			if(!StringUtils.isNumeric(strTop))
				return "";
			int top = Integer.parseInt(strTop);
			Pair<String, Integer> result = FileManager.getPlayerAtRank(top);
			if(result == null)
				return "";
			return result.getKey()+"    §7§o"+result.getValue();
		}
		if(identifier.contains("seasonpresent")) {
			LocalDate date = LocalDate.now();
			int year = date.getYear();
			int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
			String strWeek = String.format("%02d", week);
			return year+"-"+strWeek;
		}
		if(identifier.contains("seasonold")) {
			LocalDate date = LocalDate.now();
			int year = date.getYear();
			int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
			String strWeek = String.format("%02d", week);
			return year+"-"+strWeek;
		}
		return "";
	}
}
