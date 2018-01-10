package jdz.gcBoosters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import jdz.bukkitUtils.misc.Config;
import jdz.gcBoosters.data.Booster;

public class BoosterConfig {

	public static String server = "default";
	
	public static String returnCommand = "";
	public static Material noBoostersIcon = Material.PAPER;
	public static String noBoostersName = ChatColor.GRAY + "You have no boosters";
	public static List<String> noBoostersLore = Collections
			.unmodifiableList(Arrays.asList(ChatColor.GRAY + "You don't have any boosters"));
	
	public static boolean broadcast = true;
	public static String[] broadcastMessages = new String[] {"%player% has activated a %booster% booster"};
	public static boolean clickableTip = false;
	public static String clickableTipMessage = "";

	public static void reload(GCBoosters plugin) {
		FileConfiguration config = Config.getConfig(GCBoosters.instance);
		
		server = config.getString("server");
		if (server.equals(""))
			server = "default";

		returnCommand = config.getString("settings.returnCommand");
		try {
			noBoostersIcon = Material.valueOf(config.getString("settings.noBoostersIcon"));
		} catch (Exception e) {
			plugin.getLogger().warning("Invalid noBoostersIcon '" + config.getString("settings.noBoostersIcon")
					+ "' in config.yml, defaulting to paper");
			noBoostersIcon = Material.PAPER;
		}
		noBoostersName = config.getString("settings.noBoostersName");
		
		noBoostersLore = Collections
				.unmodifiableList(config.getStringList("settings.noBoosterLore"));

		broadcast = config.getBoolean("settings.broadcast");
		broadcastMessages = config.getStringList("settings.broadcastMessages").toArray(new String[1]);
		clickableTip = config.getBoolean("settings.clickableTip");
		clickableTipMessage = config.getString("settings.clickableTipMessage");
		
		Booster.clearBoosters();
		for(String boosterName: config.getConfigurationSection("boosters").getKeys(false))
			new Booster(config, boosterName);
	}
}
