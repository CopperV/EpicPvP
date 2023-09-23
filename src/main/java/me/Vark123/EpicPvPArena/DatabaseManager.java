package me.Vark123.EpicPvPArena;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseManager {

	private static Connection c;
	
	private DatabaseManager() { }
	
	public static void init() {
		Config conf = Config.get();
		Properties prop = new Properties();
		prop.setProperty("user", conf.getDbUser());
		prop.setProperty("password", conf.getDbPassword());
		prop.setProperty("autoReconnect", "true");
		try {
			c = DriverManager.getConnection("jdbc:mysql://"+conf.getDbHost()+":"+conf.getDbPort()+"/"+conf.getDb()+"?useSSL=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",prop);
		} catch (SQLException e) {
			e.printStackTrace();
			Main.getInst().getPluginLoader().disablePlugin(Main.getInst());
			return;
		}
		
		String table1 = "CREATE TABLE IF NOT EXISTS players ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "nick VARCHAR(16) NOT NULL,"
				+ "uuid TEXT NOT NULL);";
		String table2 = "CREATE TABLE IF NOT EXISTS player_stats ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "player_id INT UNIQUE NOT NULL,"
				+ "points INT,"
				+ "tokens INT,"
				+ "FOREIGN KEY (player_id) REFERENCES players(id));";

		try {
			try {
				c.setAutoCommit(false);
				c.createStatement().execute(table1);
				c.createStatement().execute(table2);
				c.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				c.rollback();
			} finally {
				c.setAutoCommit(true);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		if(c == null)
			return;
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
