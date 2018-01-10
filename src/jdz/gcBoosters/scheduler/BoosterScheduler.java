
package jdz.gcBoosters.scheduler;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.TimedTask;
import jdz.bukkitUtils.misc.utils.TimeUtils;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import jdz.gcBoosters.data.QueuedBooster;
import jdz.gcBoosters.event.BoosterEndEvent;
import jdz.gcBoosters.event.BoosterStartEvent;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class BoosterScheduler {
	@Getter private static boolean open = true;
	@Getter private static boolean hardStopped = false;
	
	private static final Map<String, Queue<QueuedBooster>> queues = new HashMap<String, Queue<QueuedBooster>>();
	private static final Map<String, QueuedBooster> activeBoosters = new HashMap<String, QueuedBooster>();
	
	private static TimedTask tickTask;
	
	private static FileLogger logger;
	
	public static void init(GCBoosters plugin) {
		tickTask = new TimedTask(GCBoosters.instance, 20*60, ()->{tick();});
		tickTask.start();
		logger = new FileLogger(plugin, "boostersLog");
	}
	
	public static Queue<QueuedBooster> getQueuedBoosters(String type){
		if (!queues.containsKey(type))
			queues.put(type, new ArrayDeque<QueuedBooster>());
		return queues.get(type);
	}
	
	public static QueuedBooster getActiveBooster(String type) {
		return activeBoosters.get(type);
	}
	
	public static void queue(OfflinePlayer player, Booster booster) {
		if (!isOpen()) return;
		
		if (BoosterDatabase.getInstance().hasBooster(player, booster))
			BoosterDatabase.getInstance().removeBooster(player, booster);
		
		QueuedBooster queuedBooster = new QueuedBooster(booster, player);
		
		if (getActiveBooster(booster.getType()) == null) {
			activate(queuedBooster);
			return;
		}
		
		if (!queues.containsKey(booster.getType()))
			queues.put(booster.getType(), new ArrayDeque<QueuedBooster>());
		queues.get(booster.getType()).add(queuedBooster);

		logger.log(player.getName()+"queued a "+booster.getName()+" booster");
		
		if (player.isOnline())
			player.getPlayer().sendMessage(ChatColor.GREEN+booster.getName()+" Booster added to the queue.");
	}

	private static void activate(QueuedBooster booster) {
		if (booster == null)
			return;
		
		activeBoosters.put(booster.getBooster().getType(), booster);
		
		booster.getBooster().executeStartCommands();
		
		if (BoosterConfig.broadcast) {
			String[] newMessages = new String[BoosterConfig.broadcastMessages.length];
			for (int i=0; i< newMessages.length; i++)
				newMessages[i] = withPlaceHolders(booster, BoosterConfig.broadcastMessages[i]);
			
			String clickableTip = withPlaceHolders(booster, BoosterConfig.clickableTipMessage);		
			TextComponent clickableTipComp = new TextComponent(clickableTip);
			clickableTipComp.setClickEvent( new ClickEvent(Action.RUN_COMMAND, "/tip "+booster.getPlayer().getName()+" "+booster.getBooster().getID()));
			
			
			for (Player player: Bukkit.getOnlinePlayers())
				player.sendMessage(newMessages);

			if (BoosterConfig.clickableTip)
				for (Player player: Bukkit.getOnlinePlayers())
					player.spigot().sendMessage(clickableTipComp);
		}
		
		
		booster.setEndTime(System.currentTimeMillis() + booster.getBooster().getDuration()*60*1000);
		
		new BoosterStartEvent(booster.getPlayer(), booster.getBooster()).call();
		
		logger.log(booster.getPlayer().getName()+"'s booster was started");
	}
	
	private static String withPlaceHolders(QueuedBooster booster, String message) {
		String s = message;
		s = s.replaceAll("%player%", booster.getPlayer().getName());
		s = s.replaceAll("%booster%", booster.getBooster().getName());
		s = s.replaceAll("%duration%", TimeUtils.timeFromMinutes(booster.getBooster().getDuration()));
		return s;
	}
	
	public static boolean dequeue(OfflinePlayer player, Booster booster) {
		if (!isOpen()) return false;
		
		return queues.get(booster.getType()).remove(new QueuedBooster(booster, player));
	}
	
	public static void softStop() {
		if (!isOpen()) return;

		if (!hardStopped)
			logger.log("A soft-stop was initiated!");
		
		for (Queue<QueuedBooster> queue: queues.values())
			for (QueuedBooster booster: queue) {
				logger.log(booster.getPlayer().getName()+"'s "+ booster.getBooster().getName() +" booster was dequeued and redeemed");
				BoosterDatabase.getInstance().addBooster(booster.getPlayer(), booster.getBooster());
			}
		
		queues.clear();
		
		open = false;
	}
	
	public static void hardStop() {
		if (!isOpen()) return;

		hardStopped = true;
		logger.log("A hard-stop was initiated!");
		softStop();

		for (QueuedBooster booster: activeBoosters.values()) {
			BoosterDatabase.getInstance().addBooster(booster.getPlayer(), booster.getBooster());
			booster.getBooster().executeEndCommands();
			logger.log(booster.getPlayer().getName()+"'s "+ booster.getBooster().getName() +" booster was halted and redeemed");
			new BoosterEndEvent(booster.getPlayer(), booster.getBooster()).call();
		}
		
		activeBoosters.clear();
		
		open = false;
	}
	
	public static void open() {
		open = true;
		hardStopped = false;
	}
	
	public static void tick() {
		for (QueuedBooster booster: activeBoosters.values())
			if (System.currentTimeMillis() > booster.getEndTime()) {
				booster.getBooster().executeEndCommands();
				new BoosterEndEvent(booster.getPlayer(), booster.getBooster()).call();
			}
		
		for (String type: queues.keySet())
			if (queues.get(type) != null && !queues.get(type).isEmpty() && getActiveBooster(type) == null)
				activate(queues.get(type).remove());
	}
}
