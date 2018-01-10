package jdz.gcBoosters;

import org.bukkit.plugin.java.JavaPlugin;

public class GCBoosters extends JavaPlugin{
	public static GCBoosters instance;

	@Override
	public void onEnable() {
		instance = this;
	}
	
	@Override
	public void onDisable() {
		// TODO Save queued boosters... or not :/
	}
}
