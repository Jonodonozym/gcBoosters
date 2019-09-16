
package jdz.gcBoosters.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static jdz.bukkitUtils.utils.ColorUtils.*;
import jdz.gcBoosters.GCBoosters;
import lombok.Getter;

public class Booster {
	private static final Map<String, Booster> boosters = new HashMap<>();
	@Getter private static final Set<String> queues = new HashSet<>();

	public static Booster get(String ID) {
		return boosters.get(ID);
	}

	public static Collection<Booster> getBoosters() {
		return boosters.values();
	}

	public static void clearBoosters() {
		boosters.clear();
		queues.clear();
	}

	@Getter private final String ID;
	@Getter private final String name;
	@Getter private final String queue;
	@Getter private final int duration;
	@Getter private final boolean tipping;
	@Getter private final boolean offlineTipping;

	@Getter private final ItemStack stack;

	@Getter private final List<String> description;

	@Getter private final List<String> tipReward;
	@Getter private final List<String> tippedReward;
	@Getter private final List<String> tipMessages;
	@Getter private final List<String> tippedMessages;

	private final List<String> commandsOnStart;
	private final List<String> commandsOnEnd;

	public void executeStartCommands(OfflinePlayer player) {
		Bukkit.getScheduler().runTask(GCBoosters.getInstance(), () -> {
			for (String command : commandsOnStart)
				if (!command.equals(""))
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders(command, player));
		});
	}

	public void executeEndCommands(OfflinePlayer player) {
		Bukkit.getScheduler().runTask(GCBoosters.getInstance(), () -> {
		for (String command : commandsOnEnd)
			if (!command.equals(""))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders(command, player));
		});
	}

	private String withPlaceholders(String command, OfflinePlayer player) {
		return command.replaceAll("%player%", player.getName());
	}

	public Booster(ConfigurationSection configSection, String ID) {
		this.ID = ID;
		name = translate(configSection.getString("name"));
		queue = configSection.getString("queue");
		duration = configSection.getInt("duration");
		tipping = configSection.getBoolean("tipping");
		offlineTipping = configSection.getBoolean("offlineTipping");

		description = Collections.unmodifiableList(translate(configSection.getStringList("description")));
		commandsOnStart = Collections.unmodifiableList(configSection.getStringList("commandsOnStart"));
		commandsOnEnd = Collections.unmodifiableList(configSection.getStringList("commandsOnEnd"));
		tipReward = Collections.unmodifiableList(configSection.getStringList("tipReward"));
		tippedReward = Collections.unmodifiableList(configSection.getStringList("tippedReward"));

		tipMessages = Collections.unmodifiableList(translate(configSection.getStringList("tipMessages")));
		tippedMessages = Collections.unmodifiableList(translate(configSection.getStringList("tippedMessages")));

		stack = generateStack(configSection);

		boosters.put(ID, this);
		queues.add(getQueue());
	}

	private final ItemStack generateStack(ConfigurationSection configSection) {
		Material mat = Material.PAPER;
		try {
			mat = Material.valueOf(configSection.getString("icon"));
		}
		catch (Exception e) {
			GCBoosters.instance.getLogger().warning("Booster " + ID + " has an invalid icon, defaulting to paper");
		}
		short data = 0;
		try {
			data = (short) configSection.getInt("iconDamage");
		}
		catch (Exception e) {}

		ItemStack stack = new ItemStack(mat, data);

		ItemMeta itemMeta = stack.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(description);

		stack.setItemMeta(itemMeta);

		return stack;
	}

}
