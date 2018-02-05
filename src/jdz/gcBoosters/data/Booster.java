
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.bukkitUtils.misc.utils.ColorUtils;
import jdz.gcBoosters.GCBoosters;
import lombok.Getter;

public class Booster {

	private static final Map<String, Booster> boosters = new HashMap<String, Booster>();
	@Getter private static final Set<String> queues = new HashSet<String>();
	
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
		for (String command: commandsOnStart)
			if (!command.equals(""))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders(command, player));
	}
	
	public void executeEndCommands(OfflinePlayer player) {
		for (String command: commandsOnEnd)
			if (!command.equals(""))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders(command, player));
	}
	
	private String withPlaceholders(String command, OfflinePlayer player) {
		return command.replaceAll("%player%", player.getName());
	}
	
	public Booster(FileConfiguration configFile, String ID) {
		this.ID = ID;
		this.name = ColorUtils.translate(configFile.getString("boosters."+ID+".name"));
		this.queue = configFile.getString("boosters."+ID+".queue");
		this.duration = configFile.getInt("boosters."+ID+".duration");
		this.tipping = configFile.getBoolean("boosters."+ID+".tipping");
		this.offlineTipping = configFile.getBoolean("boosters."+ID+".offlineTipping");

		description = Collections.unmodifiableList(ColorUtils.translate(configFile.getStringList("boosters."+ID+".description")));
		commandsOnStart = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".commandsOnStart"));
		commandsOnEnd = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".commandsOnEnd"));
		tipReward = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tipReward"));
		tippedReward = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tippedReward"));
		
		tipMessages = Collections.unmodifiableList(ColorUtils.translate(configFile.getStringList("boosters."+ID+".tipMessages")));
		tippedMessages = Collections.unmodifiableList(ColorUtils.translate(configFile.getStringList("boosters."+ID+".tippedMessages")));
		
		stack = generateStack(configFile);
		
		boosters.put(ID, this);
		queues.add(this.getQueue());
	}
	
	private final ItemStack generateStack(FileConfiguration configFile) {
		Material mat = Material.PAPER;
		try{
			mat = Material.valueOf(configFile.getString("boosters."+ID+".icon"));
		}
		catch (Exception e) {
			GCBoosters.instance.getLogger().warning("Booster "+ID+" has an invalid icon, defaulting to paper");
		}
		short data = 0;
		try {
			data = (short)configFile.getInt("boosters."+ID+".iconDamage");
		}
		catch (Exception e) {
		}
		
		ItemStack stack = new ItemStack(mat, data);
		
		ItemMeta itemMeta = stack.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(description);
		
		stack.setItemMeta(itemMeta);
		
		return stack;
	}
	
}
