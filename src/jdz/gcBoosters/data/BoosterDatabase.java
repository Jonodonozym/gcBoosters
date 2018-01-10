
package jdz.gcBoosters.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import jdz.bukkitUtils.sql.Database;
import jdz.bukkitUtils.sql.SqlColumn;
import jdz.bukkitUtils.sql.SqlColumnType;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.GCBoosters;

public class BoosterDatabase extends Database {
	public static BoosterDatabase getInstance() {
		return (BoosterDatabase) getInstance(BoosterDatabase.class);
	}

	private final String table = "gcBoosters_" + BoosterConfig.server;
	private final SqlColumn[] columns = new SqlColumn[] { new SqlColumn("player", SqlColumnType.STRING_128) };

	private final String queueTable = "gcBoosters_queue_" + BoosterConfig.server;
	private final SqlColumn[] queueTableColumns = new SqlColumn[] { new SqlColumn("player", SqlColumnType.STRING_128),
			new SqlColumn("boosterID", SqlColumnType.STRING_128), new SqlColumn("queue", SqlColumnType.STRING_64),
			new SqlColumn("order", SqlColumnType.INT_1_BYTE), new SqlColumn("endTime", SqlColumnType.LONG) };

	private final String getAllBoostersQuery;

	/**
	 * Must be initialized after boosterconfig
	 * 
	 * @param plugin
	 */
	public BoosterDatabase(GCBoosters plugin) {
		super(plugin);
		api.runOnConnect(() -> {
			api.addTable(table, columns);
			api.addTable(queueTable, queueTableColumns);
			for (Booster b : Booster.getBoosters())
				api.addColumn(table, new SqlColumn(b.getID(), SqlColumnType.INT_1_BYTE));
		});

		String query = "SELECT ";
		for (Booster b : Booster.getBoosters())
			query += b.getID() + ",";
		query = query.substring(0, query.length() - 1);
		query += " FROM " + table + " WHERE player = '%player%';";
		getAllBoostersQuery = query;
	}

	public void addPlayer(OfflinePlayer player) {
		if (!hasPlayer(player))
			api.executeUpdateAsync("INSERT INTO " + table + " (player) VALUES('" + player.getName() + "');");
	}

	public boolean hasPlayer(OfflinePlayer player) {
		List<String[]> rows = api
				.getRows("SELECT COUNT(*) FROM " + table + " WHERE player = '" + player.getName() + "';");
		return !rows.get(0)[0].equals("0");
	}

	public void addBooster(OfflinePlayer player, Booster booster) {
		api.executeUpdateAsync("UPDATE " + table + " SET " + booster.getID() + " = " + booster.getID()
				+ "+1 WHERE player = '" + player.getName() + "';");
	}

	public void removeBooster(OfflinePlayer player, Booster booster) {
		if (hasBooster(player, booster))
			api.executeUpdateAsync("UPDATE " + table + " SET " + booster.getID() + " = " + booster.getID()
					+ "-1 WHERE player = '" + player.getName() + "';");
	}

	public int getAmount(OfflinePlayer player, Booster booster) {
		List<String[]> rows = api.getRows(
				"SELECT " + booster.getID() + " FROM " + table + " WHERE player = '" + player.getName() + "';");
		return Integer.parseInt(rows.get(0)[0]);
	}

	public boolean hasBooster(OfflinePlayer player, Booster booster) {
		return getAmount(player, booster) > 0;
	}

	/**
	 * Returns a map from booster -> number owned
	 * 
	 * @param player
	 * @return
	 */
	public Map<Booster, Integer> getAllBoosters(OfflinePlayer player) {
		String query = getAllBoostersQuery.replaceAll("%player%", player.getName());
		List<String[]> rows = api.getRows(query);

		Map<Booster, Integer> boosters = new HashMap<Booster, Integer>();
		int i = 0;
		for (Booster b : Booster.getBoosters())
			boosters.put(Booster.get(b.getID()), Integer.parseInt(rows.get(0)[i++]));

		return boosters;
	}

	public void queue(QueuedBooster b) {
		String update = "INSERT INTO " + queueTable + " (player, boosterID, queue, order, endTime) VALUES('"
				+ b.getPlayer().getName() + "','" + b.getBooster().getID() + "','" + b.getBooster().getType() + "',"
				+ getNextQueuePos(b.getBooster()) + "," + b.getEndTime() + ");";
		api.executeUpdateAsync(update);
	}

	public void dequeue(QueuedBooster b) {
		String currentPosQuery = "SELECT order FROM " + queueTable + " WHERE player ='" + b.getPlayer().getName()
				+ "' AND boosterID = '" + b.getBooster().getID() + "';";
		
		int currentPos = Integer.parseInt(api.getRows(currentPosQuery).get(0)[0]);
	}

	public int getNextQueuePos(Booster b) {
		return -1; // TODO
	}
}
