
package jdz.gcBoosters.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.events.Event;
import jdz.gcBoosters.data.Booster;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoosterStartEvent extends Event {
	private final OfflinePlayer player;
	private final Booster booster;

	public static HandlerList getHandlerList() {
		return getHandlers(BoosterStartEvent.class);
	}
}
