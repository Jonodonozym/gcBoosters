
package jdz.gcBoosters.event;

import org.bukkit.OfflinePlayer;

import jdz.bukkitUtils.events.Event;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.QueuedBooster;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BoosterEvent extends Event {
	private final QueuedBooster queuedBooster;
	private final OfflinePlayer player;
	private final Booster booster;
	private final Long startTime;
	
	public BoosterEvent(QueuedBooster b) {
		this(b, b.getPlayer(), b.getBooster(), b.getStartTime());
	}
}
