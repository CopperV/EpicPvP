package me.Vark123.EpicPvPArena;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;

@Getter
public final class Config {

	private static final Config config = new Config();
	
	private String prefix;
	
	private String dbHost;
	private int dbPort;
	private String db;
	private String dbUser;
	private String dbPassword;
	
	private int pointDiffLimit;
	private int acceptanceDuration;
	private int basePointsMod;

	private int playerSaveInterval;
	
	private int countingDownDuration;
	private int maxFightTime;
	
	private Config() { }
	
	public static final Config get() {return config;}
	
	public void init() {
		File file = new File(Main.getInst().getDataFolder(), "config.yml");
		if(!file.exists())
			return;
		
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
		this.prefix = ChatColor.translateAlternateColorCodes('&', fYml.getString("prefix"));
		
		this.dbHost = fYml.getString("DB.ip");
		this.dbPort = fYml.getInt("DB.port");
		this.db = fYml.getString("DB.database");
		this.dbUser = fYml.getString("DB.user");
		this.dbPassword = fYml.getString("DB.passwd");
		
		this.pointDiffLimit = fYml.getInt("pvp.points-diff-limit");
		this.acceptanceDuration = fYml.getInt("pvp.acceptance-duration");
		this.basePointsMod = fYml.getInt("pvp.base-arena-points");
		this.countingDownDuration = fYml.getInt("pvp.counting-down-duration");
		this.maxFightTime = fYml.getInt("pvp.max-fight-time");
		
		this.playerSaveInterval = fYml.getInt("players.save-interval");
	}
	
}
