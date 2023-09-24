package me.Vark123.EpicPvPArena;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import me.Vark123.EpicPvPArena.ArenaSystem.ArenaTier;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArena;
import me.Vark123.EpicPvPArena.ArenaSystem.PvPArenaManager;
import me.Vark123.EpicPvPArena.Tools.EpicLocation;

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
	
}