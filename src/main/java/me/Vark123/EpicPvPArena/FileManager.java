package me.Vark123.EpicPvPArena;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import me.Vark123.EpicPvPArena.ArenaSystem.ArenaTier;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArena;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.Tools.EpicLocation;
import me.Vark123.EpicPvPArena.Tools.Pair;

public final class FileManager {

	@Getter
	private static final File archiveDir = new File(Main.getInst().getDataFolder(), "archive");
	
	private FileManager() { }
	
	public static void init() {
		if(!Main.getInst().getDataFolder().exists())
			Main.getInst().getDataFolder().mkdir();
		
		if(!archiveDir.exists())
			archiveDir.mkdir();
		
		Main.getInst().saveResource("config.yml", false);
		Main.getInst().saveResource("arenas.yml", false);
		Main.getInst().saveResource("tiers.yml", false);
		
		loadTiers();
		loadArenas();
		
		Config.get().init();
	}
	
	private static void loadArenas() {
		File file = new File(Main.getInst().getDataFolder(), "arenas.yml");
		if(!file.exists())
			return;
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection section = fYml.getConfigurationSection("arenas");
		if(section == null)
			return;
		section.getKeys(false).stream()
			.map(section::getConfigurationSection)
			.filter(sec -> sec != null)
			.forEach(sec -> {
				String id = sec.getString("id");
				String display = sec.getString("name");
				
				String world = sec.getString("world");
				EpicLocation resp1 = EpicLocation.builder()
						.world(world)
						.x(sec.getDouble("spawn1.x"))
						.y(sec.getDouble("spawn1.y"))
						.z(sec.getDouble("spawn1.z"))
						.build();
				EpicLocation resp2 = EpicLocation.builder()
						.world(world)
						.x(sec.getDouble("spawn2.x"))
						.y(sec.getDouble("spawn2.y"))
						.z(sec.getDouble("spawn2.z"))
						.build();
				
				PvPArenaManager.get().registerArena(PvPArena.builder()
						.id(id)
						.display(display)
						.resp1(resp1)
						.resp2(resp2)
						.build());
			});
	}
	
	private static void loadTiers() {
		File file = new File(Main.getInst().getDataFolder(), "tiers.yml");
		if(!file.exists())
			return;
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection section = fYml.getConfigurationSection("tiers");
		if(section == null)
			return;
		section.getKeys(false).stream()
			.map(section::getConfigurationSection)
			.filter(sec -> sec != null)
			.forEach(sec -> {
				String id = sec.getString("id");
				String display = sec.getString("name");
				int min = sec.getInt("min");
				int max = sec.getInt("max");
				PvPArenaManager.get().registerTier(ArenaTier.builder()
						.id(id)
						.display(display)
						.minLvl(min)
						.maxLvl(max)
						.build());
			});
	}
	
	public static void archiveRanking(String identifier) {
		File archive = new File(archiveDir, identifier+".yml");
		if(archive.exists())
			return;
		try {
			archive.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(archive);
		MutableInt posController = new MutableInt(1);
		DatabaseManager.getRanking(1000).entrySet()
			.parallelStream()
			.sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
			.forEachOrdered(entry -> {
				int pos = posController.getAndIncrement();
				Collection<Pair<String,Integer>> nicks = entry.getValue();
				int points = entry.getKey();
				fYml.set(pos+".pos", pos);
				fYml.set(pos+".points", points);
				fYml.set(pos+".nicks", nicks.stream()
						.map(pair -> pair.getKey())
						.collect(Collectors.toList()));
			});
		try {
			fYml.save(archive);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Pair<String, Integer> getPlayerAtRank(int pos) {
		MutableObject<Pair<String, Integer>> result = new MutableObject<>();
		if(!archiveDir.exists())
			result.getValue();
		Arrays.asList(archiveDir.listFiles()).stream()
			.filter(file -> file.isFile())
			.filter(file -> file.getName().endsWith(".yml"))
			.max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
			.ifPresent(file -> {
				YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
				List<Pair<String, Integer>> ranking = new LinkedList<>();
				fYml.getKeys(false).stream()
					.map(fYml::getConfigurationSection)
					.forEach(section -> {
						int points = section.getInt("points");
						section.getStringList("nicks")
							.forEach(nick -> ranking.add(new Pair<>(nick, points)));
					});
				if(pos > ranking.size())
					return;
				result.setValue(ranking.get((pos - 1)));
			});
		return result.getValue();
	}
	
}
