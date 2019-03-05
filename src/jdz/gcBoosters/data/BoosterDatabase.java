
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

import jdz.bukkitUtils.persistence.SQLColumn;
import jdz.bukkitUtils.persistence.SQLColumnType;
import jdz.bukkitUtils.persistence.SQLRow;
import jdz.bukkitUtils.persistence.minecraft.BukkitDatabase;
import jdz.bukkitUtils.utils.StringUtils;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.GCBoosters;
import lombok.Getter;

public class BoosterDatabase extends BukkitDatabase implements Listener {
	@Getter private static BoosterDatabase instance = new BoosterDatabase(GCBoosters.instance);

	private final String ownedTable = "gcBoosters_" + BoosterConfig.getServerGroup() + "_owned";
	private final SQLColumn[] ownedTableColumns = new SQLColumn[] { new SQLColumn("player", SQLColumnType.STRING_128) };

	private final String queueTable = "gcBoosters_" + BoosterConfig.getServerGroup() + "_queue";
	private final SQLColumn[] queueTableColumns = new SQLColumn[] { new SQLColumn("player", SQLColumnType.STRING_128),
			new SQLColumn("boosterID", SQLColumnType.STRING_128), new SQLColumn("queueType", SQLColumnType.STRING_64),
			new SQLColumn("queuePos", SQLColumnType.INT_1_BYTE), new SQLColumn("startTime", SQLColumnType.LONG),
			new SQLColumn("tippers", SQLColumnType.STRING) };

	private final String settingsTable = "gcBoosters_Settings";
	private final SQLColumn[] settingsTableColumns = new SQLColumn[] {
			new SQLColumn("serverGroup", SQLColumnType.STRING_128), new SQLColumn("isStopped", SQLColumnType.BOOLEAN),
			new SQLColumn("isHardStopped", SQLColumnType.BOOLEAN) };

	private final String getAllBoostersQuery;

	/**
	 * Must be initialized after boosterconfig
	 *
	 * @param plugin
	 */
	public BoosterDatabase(GCBoosters plugin) {
		super(plugin);
		runOnConnect(() -> {
			addTable(ownedTable, ownedTableColumns);
			addTable(queueTable, queueTableColumns);
			addTable(settingsTable, settingsTableColumns);
			boolean missingServer = query(
					"SELECT * FROM " + settingsTable + " WHERE serverGroup = '" + BoosterConfig.getServerGroup() + "';")
							.isEmpty();
			if (missingServer)
				update("INSERT INTO " + settingsTable + " (serverGroup) VALUES ('" + BoosterConfig.getServerGroup()
						+ "');");
			for (Booster b : Booster.getBoosters())
				addColumn(ownedTable, new SQLColumn(b.getID(), SQLColumnType.INT_1_BYTE));
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
			updateAsync("INSERT INTO " + ownedTable + " (player) VALUES('" + event.getPlayer().getName() + "');");
	}

	public boolean hasPlayer(OfflinePlayer player) {
		if (!isConnected())
			return true;
		List<SQLRow> rows = query("SELECT COUNT(*) FROM " + ownedTable + " WHERE player = '" + player.getName() + "';");
		return !rows.get(0).get(0).equals("0");
	}

	public void softStop() {
		updateAsync("UPDATE " + settingsTable + " SET isStopped = TRUE WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';");

		String query = "SELECT player, boosterID FROM " + queueTable + " WHERE queuePos != 0;";
		List<SQLRow> result = query(query);
		for (SQLRow row : result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row.get(0));
			Booster booster = Booster.get(row.get(1));
			if (booster != null)
				addBooster(player, booster);
		}

		updateAsync("DELETE FROM " + queueTable + " WHERE queuePos != 0;");
	}

	public void hardStop() {
		updateAsync("UPDATE " + settingsTable + " SET isStopped = TRUE WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';");
		updateAsync("UPDATE " + settingsTable + " SET isHardStopped = TRUE WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';");

		String query = "SELECT player, boosterID FROM " + queueTable + ";";
		List<SQLRow> result = query(query);
		for (SQLRow row : result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row.get(0));
			Booster booster = Booster.get(row.get(1));
			if (booster != null)
				addBooster(player, booster);
		}

		updateAsync("TRUNCATE TABLE " + queueTable + ";");
	}

	public void open() {
		updateAsync("UPDATE " + settingsTable + " SET isHardStopped = FALSE WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';");
		updateAsync("UPDATE " + settingsTable + " SET isStopped = FALSE WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';");
	}

	public boolean isStopped() {
		if (!isConnected())
			return true;
		return Integer.parseInt(query("SELECT isStopped FROM " + settingsTable + " WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';").get(0).get(0)) == 1;
	}

	public boolean isHardStopped() {
		if (!isConnected())
			return true;
		return Integer.parseInt(query("SELECT isHardStopped FROM " + settingsTable + " WHERE serverGroup = '"
				+ BoosterConfig.getServerGroup() + "';").get(0).get(0)) == 1;
	}

	// owned
	public void addBooster(OfflinePlayer player, Booster booster) {
		updateAsync("UPDATE " + ownedTable + " SET " + booster.getID() + " = " + booster.getID() + "+1 WHERE player = '"
				+ player.getName() + "';");
	}

	public void removeBooster(OfflinePlayer player, Booster booster) {
		if (hasBooster(player, booster))
			updateAsync("UPDATE " + ownedTable + " SET " + booster.getID() + " = " + booster.getID()
					+ "-1 WHERE player = '" + player.getName() + "';");
	}

	public int getAmount(OfflinePlayer player, Booster booster) {
		if (!isConnected())
			return 0;
		List<SQLRow> rows = query(
				"SELECT " + booster.getID() + " FROM " + ownedTable + " WHERE player = '" + player.getName() + "';");
		return Integer.parseInt(rows.get(0).get(0));
	}

	public boolean hasBooster(OfflinePlayer player, Booster booster) {
		if (!isConnected())
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
		Map<Booster, Integer> boosters = new HashMap<>();
		if (!isConnected())
			return boosters;

		String query = getAllBoostersQuery.replaceAll("%player%", player.getName());
		List<SQLRow> rows = query(query);

		int i = 0;
		for (Booster b : Booster.getBoosters())
			boosters.put(Booster.get(b.getID()), Integer.parseInt(rows.get(0).get(i++)));

		return boosters;
	}

	// queueing
	public boolean isQueued(OfflinePlayer player, Booster booster) {
		if (!isConnected())
			return false;
		String query = "SELECT player FROM " + queueTable + " WHERE player = '" + player.getName()
				+ "' AND queueType = '" + booster.getQueue() + "';";
		return !query(query).isEmpty();
	}

	public void queue(OfflinePlayer player, Booster booster) {
		String update = "INSERT INTO " + queueTable
				+ " (player, boosterID, queueType, queuePos, startTime, tippers) VALUES('" + player.getName() + "','"
				+ booster.getID() + "','" + booster.getQueue() + "'," + getNextQueuePos(booster) + ",0,'');";
		updateAsync(update);
	}

	public boolean dequeue(OfflinePlayer player, Booster booster) {
		if (!isQueued(player, booster))
			return false;

		String currentPosQuery = "SELECT queuePos FROM " + queueTable + " WHERE player = '" + player.getName()
				+ "' AND boosterID = '" + booster.getID() + "';";

		int currentPos = Integer.parseInt(query(currentPosQuery).get(0).get(0));

		String delete = "DELETE FROM " + queueTable + " WHERE player = '" + player.getName() + "' AND boosterID = '"
				+ booster.getID() + "' AND queuePos = " + currentPos + ";";

		updateAsync(delete);

		String shuntUp = "UPDATE " + queueTable + " SET queuePos = queuePos-1 WHERE queuePos > " + currentPos
				+ " AND queueType = '" + booster.getQueue() + "';";

		updateAsync(shuntUp);

		return true;
	}

	private int getNextQueuePos(Booster b) {
		if (!isConnected())
			return 0;
		String query = "SELECT queuePos from " + queueTable + " WHERE queueType = '" + b.getQueue()
				+ "' ORDER BY queuePos Desc LIMIT 1";
		List<SQLRow> result = query(query);
		if (result.isEmpty())
			return 0;
		return Integer.parseInt(result.get(0).get(0)) + 1;
	}

	// queues
	public QueuedBooster peek(String queue) {
		if (!isConnected())
			return null;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' AND queuePos = 0;";
		List<SQLRow> result = query(query);
		if (result.isEmpty())
			return null;

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(result.get(0).get(0));
		Booster booster = Booster.get(result.get(0).get(1));
		QueuedBooster queuedBooster = new QueuedBooster(booster, player);
		queuedBooster.setStartTime(Long.parseLong(result.get(0).get(2)));

		return queuedBooster;
	}

	// queues
	public QueuedBooster getNext(String queue) {
		if (!isConnected())
			return null;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' AND queuePos = 1;";
		List<SQLRow> result = query(query);
		if (result.isEmpty())
			return null;

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(result.get(0).get(0));
		Booster booster = Booster.get(result.get(0).get(1));
		QueuedBooster queuedBooster = new QueuedBooster(booster, player);
		queuedBooster.setStartTime(Long.parseLong(result.get(0).get(2)));

		return queuedBooster;
	}

	public List<QueuedBooster> getQueue(String queue) {
		List<QueuedBooster> boosters = new ArrayList<>();
		if (!isConnected())
			return boosters;
		String query = "SELECT player, boosterID, startTime FROM " + queueTable + " WHERE queueType = '" + queue
				+ "' ORDER BY queuePos ASC;";

		List<SQLRow> result = query(query);

		for (SQLRow row : result) {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(row.get(0));
			Booster booster = Booster.get(row.get(1));
			QueuedBooster queuedBooster = new QueuedBooster(booster, player);
			queuedBooster.setStartTime(Long.parseLong(row.get(2)));
			boosters.add(queuedBooster);
		}

		return boosters;
	}

	public boolean isRunning(String queue) {
		if (!isConnected())
			return false;
		String query = "SELECT player FROM " + queueTable + " WHERE queueType = '" + queue + "' && startTime != 0;";
		return !query(query).isEmpty();
	}

	public boolean isQueueEmpty(String queue) {
		if (!isConnected())
			return true;
		String query = "SELECT player FROM " + queueTable + " WHERE queueType = '" + queue + "';";
		return query(query).isEmpty();
	}

	public void activateQueued(String queue, long time) {
		String update = "UPDATE " + queueTable + " SET startTime = " + time + " WHERE queueType = '" + queue
				+ "' AND queuePos = 0;";
		updateAsync(update);
	}

	// Tipping

	public boolean hasTipped(QueuedBooster b, Player player) {
		if (!isConnected())
			return true;
		return getTippers(b).contains(player.getName());
	}

	private Set<String> getTippers(QueuedBooster b) {
		if (!isConnected())
			return new HashSet<>();
		String query = "SELECT tippers FROM " + queueTable + " WHERE player ='" + b.getPlayer().getName()
				+ "' AND boosterID = '" + b.getBooster().getID() + "';";
		List<SQLRow> result = query(query);

		return new HashSet<>(Arrays.asList(result.get(0).get(0).split(":")));
	}

	public void addTipper(QueuedBooster b, Player player) {
		Set<String> tippers = getTippers(b);
		tippers.add(player.getName());

		String update = "UPDATE " + queueTable + " SET tippers = '" + StringUtils.collectionToString(tippers, ":")
				+ "' WHERE player ='" + b.getPlayer().getName() + "' AND boosterID = '" + b.getBooster().getID() + "';";
		updateAsync(update);
	}
}
