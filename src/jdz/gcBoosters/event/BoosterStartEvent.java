
package jdz.gcBoosters.event;

import org.bukkit.event.HandlerList;

import jdz.gcBoosters.data.QueuedBooster;

public class BoosterStartEvent extends BoosterEvent {

	public BoosterStartEvent(QueuedBooster b) {
		super(b);
	}

	public static HandlerList getHandlerList() {
		return getHandlers(BoosterStartEvent.class);
	}
}
