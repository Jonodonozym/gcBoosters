
package jdz.gcBoosters.data;

import org.bukkit.OfflinePlayer;

import lombok.Data;

@Data
public class QueuedBooster {
	private final Booster booster;
	private final OfflinePlayer player;
	private Long startTime = Long.MAX_VALUE;
}
