
package jdz.gcBoosters.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.bukkitUtils.misc.utils.ColorUtils;
import jdz.gcBoosters.GCBoosters;
import lombok.Getter;

public class Booster {

	private static final Map<String, Booster> boosters = new HashMap<String, Booster>();
	
	public static Booster get(String ID) {
		return boosters.get(ID);
	}
	
	public static Collection<Booster> getBoosters() {
		return boosters.values();
	}
	
	public static void clearBoosters() {
		boosters.clear();
	}
	
	@Getter private final String ID;
	@Getter private final String name;
	@Getter private final String type;
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
	
	public void executeStartCommands() {
		for (String command: commandsOnStart)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public void executeEndCommands() {
		for (String command: commandsOnEnd)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public Booster(FileConfiguration configFile, String ID) {
		this.ID = ID;
		this.name = ColorUtils.translate(configFile.getString("boosters."+ID+".name"));
		this.type = configFile.getString("boosters."+ID+".type");
		this.duration = configFile.getInt("boosters."+ID+".duration");
		this.tipping = configFile.getBoolean("boosters."+ID+".tipping");
		this.offlineTipping = configFile.getBoolean("boosters."+ID+".offlineTipping");

		description = Collections.unmodifiableList(ColorUtils.translate(configFile.getStringList("boosters."+ID+".description")));
		commandsOnStart = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".commandsOnStart"));
		commandsOnEnd = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".commandsOnEnd"));
		tipReward = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tipReward"));
		tippedReward = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tippedReward"));
		
		tipMessages = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tipMessages"));
		tippedMessages = Collections.unmodifiableList(configFile.getStringList("boosters."+ID+".tippedMessages"));
		
		stack = generateStack(configFile);
		
		boosters.put(ID, this);
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
