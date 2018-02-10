
package jdz.gcBoosters.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.sql.Database;
import jdz.bukkitUtils.sql.SqlColumn;
import jdz.bukkitUtils.sql.SqlColumnType;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.GCBoosters;
import lombok.Getter;

public class BoosterDatabase extends Database implements Listener{
	@Getter private static BoosterDatabase instance = new BoosterDatabase(GCBoosters.instance);

	private final String ownedTable = "gcBoosters_" + BoosterConfig.serverGroup + "_owned";
	private final SqlColumn[] ownedTableColumns = new SqlColumn[] { new SqlColumn("player", SqlColumnType.STRING_128) };

	private final String queueTable = "gcBoosters_" + BoosterConfig.serverGroup + "_queue";
	private final SqlColumn[] queueTableColumns = new SqlColumn[] { new SqlColumn("player", SqlColumnType.STRING_128),
			new SqlColumn("boosterID", SqlColumnType.STRING_128), new SqlColumn("queueType", SqlColumnType.STRING_64),
			new SqlColumn("queuePos", SqlColumnType.INT_1_BYTE), new SqlColumn("startTime", SqlColumnType.LONG),
			new SqlColumn("tippers", SqlColumnType.STRING) };

	private final String settingsTable = "gcBoosters_Settings";
	private final SqlColumn[] settingsTableColumns = new SqlColumn[] {
			new SqlColumn("serverGroup", SqlColumnType.STRING_128), new SqlColumn("isStopped", SqlColumnType.BOOLEAN), new SqlColumn("isHardStopped", SqlColumnType.BOOLEAN) };

	private final String getAllBoostersQuery;

	/**
	 * Must be initialized after boosterconfig
	 * 
	 * @param plugin
	 */
	public BoosterDatabase(GCBoosters plugin) {
		super(plugin);
		api.runOnConnect(() -> {
			api.addTable(ownedTable, ownedTableColumns);
			api.addTable(queueTable, queueTableColumns);
			api.addTable(settingsTable, settingsTableColumns);
			boolean missingServer = api
					.getRows("SELECT * FROM " + settingsTable + " WHERE serverGroup = '" + BoosterConfig.serverGroup + "';")
					.isEmpty();
			if (missingServer)
				api.executeUpdate(
						"INSERT INTO " + settingsTable + " (serverGroup) VALUES ('" + BoosterConfig.serverGroup + "');");
			for (Booster b : Booster.getBoosters())
				api.addColumn(ownedTable, new SqlColumn(b.getID(), SqlColumnType.INT_1_BYTE));
		});

		String query = "SELECT ";
		for (Booster b : Booster.getBoosters())
			query += b.getID() + ",";
		query = query.substring(0, query.length() - 1);
		query += " FROM " + ownedTable + " WHERE player = '%player%';";
		getAllBoostersQuery = query;
		
		Bukkit.getPluginManager().registerEvents(this, GCBoosters.instance);
	}

	@EventHandler
	public void loginEvent(PlayerJoinEvent event) {
		if (!hasPlayer(event.getPlayer()))
			api.executeUpdateAsync("INSERT INTO " + ownedTable + " (player) VALUES('" + event.getPlayer().getName() + "');");
	}

	public boolean hasPlayer(OfflinePlayer player) {
		if (!api.isConnected())
			return true;
		List<String[]> rows = api
				.getRows("SELECT COUNT(*) FROM " + ownedTable + " WHERE player = '" + player.getName() + "';");
		return !rows.get(0)[0].equals("0");
	}
	
	public void softStop() {
		api.executeUpdateAsync("UPDATE " + settingsTable + " SET isStopped = TRUE WHERE serverGroup = '"+BoosterConfig.serverGroup+"';");
	
		String query = "SELECT player, boosterID FROM "+queueTable+" WHERE queuePos != 0;";
		List<String[]> result = api.getRows(query);
		for(String[] row: result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row[0]);
			Booster booster = Booster.get(row[1]);
			if (booster != null)
				addBooster(player, booster);
		}
		
		api.executeUpdateAsync("DELETE FROM "+queueTable+" WHERE queuePos != 0;");
	}

	public void hardStop() {
		api.executeUpdateAsync("UPDATE " + settingsTable + " SET isStopped = TRUE WHERE serverGroup = '"+BoosterConfig.serverGroup+"';");
		api.executeUpdateAsync("UPDATE " + settingsTable + " SET isHardStopped = TRUE WHERE serverGroup = '"+BoosterConfig.serverGroup+"';");
		
		String query = "SELECT player, boosterID FROM "+queueTable+";";
		List<String[]> result = api.getRows(query);
		for(String[] row: result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row[0]);
			Booster booster = Booster.get(row[1]);
			if (booster != null)
				addBooster(player, booster);
		}
		
		api.executeUpdateAsync("TRUNCATE TABLE "+queueTable+";");
	}

	public void open() {
		api.executeUpdateAsync("UPDATE " + settingsTable + " SET isHardStopped = FALSE WHERE serverGroup = '"+BoosterConfig.serverGroup+"';");
		api.executeUpdateAsync("UPDATE " + settingsTable + " SET isStopped = FALSE WHERE serverGroup = '"+BoosterConfig.serverGroup+"';");
	}
	
	public boolean isStopped() {
		if (!api.isConnected())
			return true;
		return Integer.parseInt(api.getRows("SELECT isStopped FROM "+settingsTable+" WHERE serverGroup = '"+BoosterConfig.serverGroup+"';").get(0)[0]) == 1;
	}
	
	public boolean isHardStopped() {
		if (!api.isConnected())
			return true;
		return Integer.parseInt(api.getRows("SELECT isHardStopped FROM "+settingsTable+" WHERE serverGroup = '"+BoosterConfig.serverGroup+"';").get(0)[0]) == 1;
	}

	// owned
	public void addBooster(OfflinePlayer player, Booster booster) {
		api.executeUpdateAsync("UPDATE " + ownedTable + " SET " + booster.getID() + " = " + booster.getID()
				+ "+1 WHERE player = '" + player.getName() + "';");
	}

	public void removeBooster(OfflinePlayer player, Booster booster) {
		if (hasBooster(player, booster))
			api.executeUpdateAsync("UPDATE " + ownedTable + " SET " + booster.getID() + " = " + booster.getID()
					+ "-1 WHERE player = '" + player.getName() + "';");
	}

	public int getAmount(OfflinePlayer player, Booster booster) {
		if (!api.isConnected())
			return 0;
		List<String[]> rows = api.getRows(
				"SELECT " + booster.getID() + " FROM " + ownedTable + " WHERE player = '" + player.getName() + "';");
		return Integer.parseInt(rows.get(0)[0]);
	}

	public boolean hasBooster(OfflinePlayer player, Booster booster) {
		if (!api.isConnected())
			return false;
		return getAmount(player, booster) > 0;
	}

	/**
	 * Returns a map from booster -> number owned
	 * 
	 * @param player
	 * @return
	 */
	public Map<Booster, Integer> getAllBoosters(OfflinePlayer player) {
		Map<Booster, Integer> boosters = new HashMap<Booster, Integer>();
		if (!api.isConnected())
			return boosters;
		
		String query = getAllBoostersQuery.replaceAll("%player%", player.getName());
		List<String[]> rows = api.getRows(query);

		int i = 0;
		for (Booster b : Booster.getBoosters())
			boosters.put(Booster.get(b.getID()), Integer.parseInt(rows.get(0)[i++]));

		return boosters;
	}

	// queueing
	public boolean isQueued(OfflinePlayer player, Booster booster) {
		if (!api.isConnected())
			return false;
		String query = "SELECT player FROM " + queueTable + " WHERE player = '" + player.getName()
				+ "' AND queueType = '" + booster.getQueue() + "';";
		return !api.getRows(query).isEmpty();
	}

	public void queue(OfflinePlayer player, Booster booster) {
		String update = "INSERT INTO " + queueTable + " (player, boosterID, queueType, queuePos, startTime, tippers) VALUES('"
				+ player.getName() + "','" + booster.getID() + "','" + booster.getQueue() + "',"
				+ getNextQueuePos(booster) + ",0,'');";
		api.executeUpdateAsync(update);
	}

	public boolean dequeue(OfflinePlayer player, Booster booster) {
		if (!isQueued(player, booster))
			return false;

		String currentPosQuery = "SELECT queuePos FROM " + queueTable + " WHERE player = '" + player.getName()
				+ "' AND boosterID = '" + booster.getID() + "';";

		int currentPos = Integer.parseInt(api.getRows(currentPosQuery).get(0)[0]);

		String delete = "DELETE FROM " + queueTable + " WHERE player = '" + player.getName() + "' AND boosterID = '"
				+ booster.getID() + "' AND queuePos = " + currentPos + ";";

		api.executeUpdateAsync(delete);

		String shuntUp = "UPDATE " + queueTable + " SET queuePos = queuePos-1 WHERE queuePos > " + currentPos
				+ " AND queueType = '" + booster.getQueue() + "';";

		api.executeUpdateAsync(shuntUp);

		return true;
	}

	private int getNextQueuePos(Booster b) {
		if (!api.isConnected())
			return 0;
		String query = "SELECT queuePos from " + queueTable + " WHERE queueType = '" + b.getQueue()
				+ "' ORDER BY queuePos Desc LIMIT 1";
		List<String[]> result = api.getRows(query);
		if (result.isEmpty())
			return 0;
		return Integer.parseInt(result.get(0)[0]) + 1;
	}

	// queues
	public QueuedBooster peek(String queue) {
		if (!api.isConnected())
			return null;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' AND queuePos = 0;";
		List<String[]> result = api.getRows(query);
		if (result.isEmpty())
			return null;

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(result.get(0)[0]);
		Booster booster = Booster.get(result.get(0)[1]);
		QueuedBooster queuedBooster = new QueuedBooster(booster, player);
		queuedBooster.setStartTime(Long.parseLong(result.get(0)[2]));

		return queuedBooster;
	}
	
	// queues
	public QueuedBooster getNext(String queue) {
		if (!api.isConnected())
			return null;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' AND queuePos = 1;";
		List<String[]> result = api.getRows(query);
		if (result.isEmpty())
			return null;

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(result.get(0)[0]);
		Booster booster = Booster.get(result.get(0)[1]);
		QueuedBooster queuedBooster = new QueuedBooster(booster, player);
		queuedBooster.setStartTime(Long.parseLong(result.get(0)[2]));

		return queuedBooster;
	}

	public List<QueuedBooster> getQueue(String queue) {
		List<QueuedBooster> boosters = new ArrayList<QueuedBooster>();
		if (!api.isConnected())
			return boosters;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' ORDER BY queuePos ASC;";

		List<String[]> result = api.getRows(query);

		for (String[] row : result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row[0]);
			Booster booster = Booster.get(row[1]);
			QueuedBooster queuedBooster = new QueuedBooster(booster, player);
			queuedBooster.setStartTime(Long.parseLong(row[2]));
			boosters.add(queuedBooster);
		}

		return boosters;
	}

	public boolean isRunning(String queue) {
		if (!api.isConnected())
			return false;
		String query = "SELECT player FROM " + queueTable + " WHERE queueType = '" + queue + "' && startTime != 0;";
		return !api.getRows(query).isEmpty();
	}

	public boolean isQueueEmpty(String queue) {
		if (!api.isConnected())
			return true;
		String query = "SELECT player FROM " + queueTable + " WHERE queueType = '" + queue + "';";
		return api.getRows(query).isEmpty();
	}

	public void activateQueued(String queue, long time) {
		String update = "UPDATE " + queueTable + " SET startTime = " + time
				+ " WHERE queueType = '" + queue + "' AND queuePos = 0;";
		api.executeUpdateAsync(update);
	}

	// Tipping

	public boolean hasTipped(QueuedBooster b, Player player) {
		if (!api.isConnected())
			return true;
		return getTippers(b).contains(player.getName());
	}

	private Set<String> getTippers(QueuedBooster b) {
		if (!api.isConnected())
			return new HashSet<String>();
		String query = "SELECT tippers FROM " + queueTable + " WHERE player ='" + b.getPlayer().getName()
				+ "' AND boosterID = '" + b.getBooster().getID() + "';";
		List<String[]> result = api.getRows(query);

		return new HashSet<String>(Arrays.asList(result.get(0)[0].split(":")));
	}

	public void addTipper(QueuedBooster b, Player player) {
		Set<String> tippers = getTippers(b);
		tippers.add(player.getName());

		String update = "UPDATE " + queueTable + " SET tippers = '" + StringUtils.collectionToString(tippers, ":")
				+ "' WHERE player ='" + b.getPlayer().getName() + "' AND boosterID = '" + b.getBooster().getID() + "';";
		api.executeUpdateAsync(update);
	}
}
