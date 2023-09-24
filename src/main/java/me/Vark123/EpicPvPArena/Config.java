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
	
	private int pointDiffLimit = 500;
	private int acceptanceDuration = 60;
	private int basePointsMod = 11;
	
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
	}
	
}
