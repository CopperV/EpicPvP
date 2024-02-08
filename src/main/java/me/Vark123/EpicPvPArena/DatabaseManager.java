package me.Vark123.EpicPvPArena;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.PlayerSystem.PvPPlayerManager;
import me.Vark123.EpicPvPArena.Tools.Pair;

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
			c = DriverManager.getConnection("jdbc:mysql://"+conf.getDbHost()+":"+conf.getDbPort()+"/"+conf.getDb()+"?useSSL=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&allowPublicKeyRetrieval=true",prop);
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
				+ "fights INT,"
				+ "wins INT,"
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
		int fights = 0;
		int wins = 0;
		
		String sql = "SELECT player_stats.points AS POINTS, player_stats.tokens AS TOKENS, "
				+ "player_stats.fights AS FIGHTS, player_stats.wins AS WINS from player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "WHERE players.uuid LIKE \""+p.getUniqueId().toString()+"\";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			if(set.next()) {
				points = set.getInt("POINTS");
				tokens = set.getInt("TOKENS");
				fights = set.getInt("FIGHTS");
				wins = set.getInt("WINS");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return EpicPvPPlayer.builder()
				.player(p)
				.points(points)
				.tokens(tokens)
				.fights(fights)
				.wins(wins)
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
		int fights = pp.getFights();
		int wins = pp.getWins();
		String call = "CALL SavePlayer("+id+","+points+","+tokens+","+fights+","+wins+");";
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
		return getPlayerId(p.getUniqueId());
	}
	
	private static int getPlayerId(UUID uid) {
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
	
	public static void resetRanking() {
		List<String> tokenSqls = new LinkedList<>();
		MutableInt posController = new MutableInt(1);
		DatabaseManager.getRanking(900, 10).entrySet()
			.parallelStream()
			.sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
			.forEachOrdered(entry -> {
				int pos = posController.getAndIncrement();
				Collection<Pair<String, Integer>> nicks = entry.getValue();
				
				MutableInt tokens = new MutableInt(0);
				if(pos == 1) tokens.setValue(13);
				else if(pos == 2) tokens.setValue(10);
				else if(pos == 3) tokens.setValue(7);
				else if(pos >= 4 && pos <= 10) tokens.setValue(5);
				else if(pos >= 11 && pos <= 25) tokens.setValue(4);
				else if(pos >= 26 && pos <= 50) tokens.setValue(3);
				else if(pos >= 51 && pos <= 100) tokens.setValue(2);
				else if(pos >= 101 && pos <= 250) tokens.setValue(1);
				
				if(tokens.getValue() <= 0)
					return;
				
				nicks.stream()
					.forEach(pair -> {
						String nick = pair.getKey();
						int presentTokens = pair.getValue();
						OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(nick);
						if(offPlayer.isOnline()) {
							Player p = offPlayer.getPlayer();
							PvPPlayerManager.get().getPvPPlayer(p)
								.ifPresent(pp -> {
									pp.setPoints(1000);
									pp.resetFights();
									pp.addTokens(tokens.getValue());
									p.sendMessage("§7["+Config.get().getPrefix()+"§7] "
											+ "§eOtrzymujesz §7"+tokens.getValue()+" §etokenow gladiatora!");
								});
						}
						UUID uid = offPlayer.getUniqueId();
						int id = getPlayerId(uid);
						if(id < 0)
							return;
						String sql = "UPDATE player_stats "
								+ "SET player_stats.tokens = "+(presentTokens+tokens.getValue())+" "
								+ "WHERE player_stats.player_id = "+id+";";
						tokenSqls.add(sql);
					});
		});
		String resetSql = "UPDATE player_stats SET player_stats.points = 1000, player_stats.fights = 0, player_stats.wins = 0;";
		try {
			try {
				c.setAutoCommit(false);
				for(String sql : tokenSqls)
					c.createStatement().executeUpdate(sql);
				c.createStatement().executeUpdate(resetSql);
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
		try {
			c.createStatement().executeUpdate(resetSql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Collection<Pair<String, Integer>>> getRanking(int bound) {
		Map<Integer, Collection<Pair<String, Integer>>> ranking = new LinkedHashMap<>();
		
		String sql = "SELECT players.nick AS nick, player_stats.points AS points, player_stats.tokens AS tokens "
				+ "FROM player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "WHERE points > "+bound+" "
				+ "ORDER BY points DESC;";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			while(set.next()) {
				String nick = set.getString("nick");
				int points = set.getInt("points");
				int tokens = set.getInt("tokens");
				Collection<Pair<String, Integer>> tmp = ranking.getOrDefault(points, new LinkedList<>());
				tmp.add(new Pair<>(nick, tokens));
				ranking.put(points, tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ranking;
	}
	public static Map<Integer, Collection<Pair<String, Integer>>> getRanking(int bound, int fights) {
		Map<Integer, Collection<Pair<String, Integer>>> ranking = new LinkedHashMap<>();
		
		String sql = "SELECT players.nick AS nick, player_stats.points AS points, player_stats.tokens AS tokens, player_stats.fights AS fights "
				+ "FROM player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "WHERE points > "+bound+" AND fights > "+fights+" "
				+ "ORDER BY points DESC;";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			while(set.next()) {
				String nick = set.getString("nick");
				int points = set.getInt("points");
				int tokens = set.getInt("tokens");
				Collection<Pair<String, Integer>> tmp = ranking.getOrDefault(points, new LinkedList<>());
				tmp.add(new Pair<>(nick, tokens));
				ranking.put(points, tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ranking;
	}
	
	public static Pair<String, Integer> getPlayerAtRank(int pos) {
		String sql = "SELECT players.nick AS nick, player_stats.points AS points "
				+ "FROM player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "ORDER BY points DESC "
				+ "LIMIT 1 "
				+ "OFFSET "+(pos-1)+";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			if(set.next()) {
				String nick = set.getString("nick");
				int points = set.getInt("points");
				return new Pair<>(nick, points);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Pair<String, Integer> getPlayerAtRank(int pos, int bound, int fights) {
		String sql = "SELECT players.nick AS nick, player_stats.points AS points "
				+ "FROM player_stats "
				+ "INNER JOIN players ON player_stats.player_id = players.id "
				+ "WHERE points > "+bound+" AND fights > "+fights+" "
				+ "ORDER BY points DESC "
				+ "LIMIT 1 "
				+ "OFFSET "+(pos-1)+";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			if(set.next()) {
				String nick = set.getString("nick");
				int points = set.getInt("points");
				return new Pair<>(nick, points);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
