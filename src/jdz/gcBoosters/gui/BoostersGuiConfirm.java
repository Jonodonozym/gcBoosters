
package jdz.gcBoosters.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.components.guiMenu.guis.GuiMenuConfirmDialogue;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.commands.member.BoosterStartCommand;
import jdz.gcBoosters.data.Booster;

public class BoostersGuiConfirm extends GuiMenuConfirmDialogue {
	private final Booster b;
	private final BoostersGUI superMenu;

	public BoostersGuiConfirm(Player player, Booster b, BoostersGUI superMenu) {
		super(GCBoosters.instance, ChatColor.RED + "Are you sure?");
		this.b = b;
		this.superMenu = superMenu;
	}

	@Override
	public void onConfirm(Player player) {
		BoosterStartCommand.startBooster(player, player, b);
		player.closeInventory();
	}

	@Override
	public void onCancel(Player player) {
		superMenu.open(player);
	}
}
