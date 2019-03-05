package jdz.gcBoosters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.configuration.AutoConfig;
import jdz.bukkitUtils.configuration.ConfigReloadEvent;
import jdz.bukkitUtils.utils.ItemUtils;
import jdz.gcBoosters.data.Booster;
import lombok.Getter;

public class BoosterConfig extends AutoConfig {
	@Getter private static String serverGroup = "default";

	@Getter private static String returnCommand = "";
	private static Material noBoostersIcon = Material.PAPER;
	private static String noBoostersName = ChatColor.GRAY + "You have no boosters";
	private static List<String> noBoostersLore = Collections
			.unmodifiableList(Arrays.asList(ChatColor.GRAY + "You don't have any boosters"));

	public static ItemStack getNoBoostersIcon() {
		ItemStack item = new ItemStack(noBoostersIcon);
		ItemUtils.setName(item, noBoostersName);
		ItemUtils.setLore(item, noBoostersLore);
		return item;
	}

	@Getter private static boolean broadcastEnabled = true;
	@Getter private static List<String> broadcastStartMessages = Arrays
			.asList("%player% has activated a %booster% booster");
	@Getter private static List<String> broadcastEndMessages = Arrays.asList("%player%'s %booster% booster has ended");
	@Getter private static boolean clickableTipEnabled = false;
	@Getter private static String clickableTipMessage = "";

	public BoosterConfig(Plugin plugin) {
		super(plugin, "settings");
	}

	@Override
	public void onConfigReload(ConfigReloadEvent event) {
		if (!eventApplies(event))
			return;
		super.onConfigReload(event);

		Booster.clearBoosters();
		for (String boosterName : event.getConfig().getConfigurationSection("boosters").getKeys(false))
			new Booster(event.getConfig(), boosterName);
	}
}
