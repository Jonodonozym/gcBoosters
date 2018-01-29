
package jdz.gcBoosters.event;

import org.bukkit.event.HandlerList;

import jdz.gcBoosters.data.QueuedBooster;

public class BoosterEndEvent extends BoosterEvent {
	public BoosterEndEvent(QueuedBooster b) {
		super(b);
	}

	public static HandlerList getHandlerList() {
		return getHandlers(BoosterEndEvent.class);
	}
}
