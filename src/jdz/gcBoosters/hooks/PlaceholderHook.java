
package jdz.gcBoosters.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.misc.utils.TimeUtils;
import jdz.gcBoosters.data.QueuedBooster;
import jdz.gcBoosters.tasks.BoosterQueueChecker;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

@AllArgsConstructor
public class PlaceholderHook extends PlaceholderExpansion {
	private final Plugin plugin;

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (identifier.startsWith("booster_active_booster"))
			return getActiveBooster(identifier.substring("booster_active_booster".length()));

		if (identifier.startsWith("booster_next_booster"))
			return getNextBooster(identifier.substring("booster_next_booster".length()));

		if (identifier.startsWith("booster_time_left"))
			return getTimeLeft(identifier.substring("booster_time_left".length()));

		return null;
	}

	private String getActiveBooster(String queue) {
		if (queue.equals(""))
			queue = "default";
		QueuedBooster b = BoosterQueueChecker.getInstance().getActiveBoosters().get(queue);
		if (b == null)
			return "";
		return b.getBooster().getName();
	}

	private String getNextBooster(String queue) {
		if (queue.equals(""))
			queue = "default";
		QueuedBooster b = BoosterQueueChecker.getInstance().getNextBoosters().get(queue);
		if (b == null)
			return "";
		return b.getBooster().getName();
	}

	private String getTimeLeft(String queue) {
		if (queue.equals(""))
			queue = "default";
		QueuedBooster b = BoosterQueueChecker.getInstance().getActiveBoosters().get(queue);
		if (b == null)
			return "";
		return TimeUtils.timeFromMinutes((int) (b.getStartTime() / 1000 / 60 + b.getBooster().getDuration()
				- System.currentTimeMillis() / 1000 / 60));
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().get(0);
	}

	@Override
	public String getIdentifier() {
		return plugin.getName();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
}

