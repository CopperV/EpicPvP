package me.Vark123.EpicPvPArena;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.entity.Player;

import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;

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
	
	public static EpicPvPPlayer loadPlayer(Player p) {
		int points = 1000;
		int tokens = 0;
		
		String sql = "SELECT player_stats.points AS POINTS, player_stats.tokens AS TOKENS from player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "WHERE players.uuid LIKE \""+p.getUniqueId().toString()+"\";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			if(set.next()) {
				points = set.getInt("POINTS");
				tokens = set.getInt("TOKENS");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return EpicPvPPlayer.builder()
				.player(p)
				.points(points)
				.tokens(tokens)
				.build();
	}
	
	public static void savePlayer(EpicPvPPlayer pp) {
		Player p = pp.getPlayer();
		if(!isPlayerExistsInDatabase(p))
			addPlayerToDatabase(p);
		int id = getPlayerId(p);
		if(id < 0)
			return;
		
		int points = pp.getPoints();
		int tokens = pp.getTokens();
		String call = "CALL SavePlayer("+id+","+points+","+tokens+");";
		try {
			c.createStatement().execute(call);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isPlayerExistsInDatabase(Player p) {
		String uid = p.getUniqueId().toString();
		String sql = "SELECT id FROM players WHERE uuid LIKE \""+uid+"\";";
		try {
			return c.createStatement().executeQuery(sql).next();
		} catch (SQLException e) {
			return false;
		}
	}
	
	private static void addPlayerToDatabase(Player p) {
		String sql = "INSERT INTO `players` (nick, uuid) "
				+ "VALUES (\""+p.getName()+"\",\""+p.getUniqueId().toString()+"\");";
		try {
			c.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static int getPlayerId(Player p) {
		String uid = p.getUniqueId().toString();
		String sql = "SELECT id FROM players WHERE uuid LIKE \""+uid+"\";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			set.next();
			return set.getInt("id");
		} catch (SQLException e) {
			return -1;
		}
	}
	
	public static int getPlayerRanking(Player player) {
		int pos = -1;
		
		String sql = "SELECT tmp.row_number AS pos FROM "
				+ "(SELECT (@row_number := @row_number + 1) AS `row_number`, p.uuid AS uuid "
				+ "FROM (SELECT players.uuid AS uuid, player_stats.points AS points FROM players INNER JOIN player_stats ON players.id = player_stats.player_id ORDER BY player_stats.points DESC) AS p "
				+ "JOIN (SELECT @row_number := 0) r) AS tmp "
				+ "INNER JOIN players ON tmp.uuid = players.uuid "
				+ "WHERE players.uuid LIKE '"+player.getUniqueId().toString()+"';";
		
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			if(set.next())
				pos = set.getInt("pos");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pos;
	}
	
	public static Map<String, Integer> getPvPRanking(int page) {
		Map<String, Integer> ranking = new LinkedHashMap<>();
		
		String sql = "SELECT players.nick AS nick, player_stats.points AS points FROM players "
				+ "INNER JOIN player_stats ON players.id = player_stats.player_id "
				+ "ORDER BY player_stats.points DESC "
				+ "LIMIT "+(page*10)+", 10";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			while(set.next()) {
				String nick = set.getString("nick");
				int points = set.getInt("points");
				ranking.put(nick, points);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ranking;
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
