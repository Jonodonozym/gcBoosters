package jdz.gcBoosters.tasks;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import jdz.bukkitUtils.misc.utils.TimeUtils;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.event.BoosterEndEvent;
import jdz.gcBoosters.event.BoosterEvent;
import jdz.gcBoosters.event.BoosterStartEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class BoosterBroadcaster implements Listener{
	
	@EventHandler
	public void onBoosterStart(BoosterStartEvent event) {
		if (System.currentTimeMillis() - event.getStartTime() > 120000)
			return;
		
		String[] newMessages = new String[BoosterConfig.broadcastStartMessages.length];
		for (int i=0; i< newMessages.length; i++)
			newMessages[i] = withPlaceHolders(BoosterConfig.broadcastStartMessages[i], event);
		
		Bukkit.getConsoleSender().sendMessage(newMessages);
		
		if (BoosterConfig.broadcast) {
			
			for (Player player: Bukkit.getOnlinePlayers())
				player.sendMessage(newMessages);
	
			if (BoosterConfig.clickableTip) {
				String clickableTip = withPlaceHolders(BoosterConfig.clickableTipMessage, event);		
				TextComponent clickableTipComp = new TextComponent(clickableTip);
				clickableTipComp.setClickEvent( new ClickEvent(Action.RUN_COMMAND, "/tip "+event.getPlayer().getName()+" "+event.getBooster().getID()));
				
				for (Player player: Bukkit.getOnlinePlayers())
					player.spigot().sendMessage(clickableTipComp);
			}
		}
	}
	
	@EventHandler
	public void onBoosterEnd(BoosterEndEvent event) {
		String[] newMessages = new String[BoosterConfig.broadcastEndMessages.length];
		for (int i=0; i< newMessages.length; i++)
			newMessages[i] = withPlaceHolders(BoosterConfig.broadcastEndMessages[i], event);
		
		if (BoosterConfig.broadcast)
			for (Player player: Bukkit.getOnlinePlayers())
				player.sendMessage(newMessages);
		
		Bukkit.getConsoleSender().sendMessage(newMessages);
	}
	
	private String withPlaceHolders(String message, BoosterEvent event) {
		String s = message;
		s = s.replaceAll("%player%", event.getPlayer().getName());
		s = s.replaceAll("%booster%", event.getBooster().getName());
		s = s.replaceAll("%duration%", TimeUtils.timeFromMinutes(event.getBooster().getDuration()));
		return s;
	}
}
