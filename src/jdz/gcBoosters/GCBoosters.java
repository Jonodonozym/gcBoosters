package jdz.gcBoosters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.configuration.ConfigReloadEvent;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.gcBoosters.commands.admin.ABoosterCommandExecutor;
import jdz.gcBoosters.commands.member.BoosterCommandExecutor;
import jdz.gcBoosters.data.BoosterDatabase;
import jdz.gcBoosters.hooks.PlaceholderHook;
import jdz.gcBoosters.tasks.BoosterBroadcaster;
import jdz.gcBoosters.tasks.BoosterQueueChecker;
import lombok.Getter;

public class GCBoosters extends JavaPlugin {
	@Getter public static GCBoosters instance;

	@Override
	public void onEnable() {
		instance = this;

		new BoosterConfig(this).register();
		new ConfigReloadEvent(this).call();

		new BoosterCommandExecutor(this).register();
		new ABoosterCommandExecutor(this).register();

		BoosterDatabase.getInstance();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new BoosterBroadcaster(), this);

		BoosterQueueChecker.getInstance();

		for (RegisteredListener l : HandlerList.getRegisteredListeners(this))
			try {
				for (Player p : Bukkit.getOnlinePlayers())
					l.callEvent(new PlayerJoinEvent(p, ""));
			}
			catch (EventException e) {
				new FileLogger(this).createErrorLog(e);
			}

		new PlaceholderHook().register();
	}
}
