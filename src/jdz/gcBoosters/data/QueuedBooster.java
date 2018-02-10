
package jdz.gcBoosters.data;

import org.bukkit.OfflinePlayer;

import lombok.Data;

@Data
public class QueuedBooster {
	private final Booster booster;
	private final OfflinePlayer player;
	private Long startTime = Long.MAX_VALUE;
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QueuedBooster))
			return false;
		QueuedBooster ob = (QueuedBooster)other;
		return ob.player.getName().equals(player.getName()) && ob.booster.equals(booster) && ob.startTime.equals(startTime);
	}
	
	@Override
	public int hashCode() {
		return booster.hashCode() * player.hashCode();
	}
}
