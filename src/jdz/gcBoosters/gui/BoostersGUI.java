
package jdz.gcBoosters.gui;

import java.util.Arrays;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.Damageable;

import jdz.bukkitUtils.components.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStackCommands;
import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStackLinkedMenu;
import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStackNothing;
import jdz.bukkitUtils.utils.ItemUtils;
import jdz.gcBoosters.BoosterConfig;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;

public class BoostersGUI extends GuiMenu {
	private final Player player;
	private final Inventory inv;

	public BoostersGUI(Player player) {
		super(GCBoosters.instance);
		this.player = player;

		inv = Bukkit.createInventory(player, 36, ChatColor.AQUA + "My Boosters");
		refreshStacks();
	}

	private void refreshStacks() {
		clear(inv);

		ClickableStackCommands returnArrow = new ClickableStackCommands(Material.ARROW,
				ChatColor.AQUA + (BoosterConfig.getReturnCommand().equals("") ? "Exit" : "Return"), false,
				Arrays.asList(BoosterConfig.getReturnCommand()));

		if (BoosterConfig.getReturnCommand().equals(""))
			returnArrow.closeOnClick();

		setItem(returnArrow, 31, inv);

		Bukkit.getScheduler().runTaskAsynchronously(GCBoosters.instance, () -> {
			int i = 0;
			Map<Booster, Integer> boosters = BoosterDatabase.getInstance().getAllBoosters(player);

			for (Booster b : boosters.keySet())
				for (int j = 0; j < boosters.get(b); j++)
					setItem(getBoosterStack(b), i++, inv);

			if (i == 0)
				setItem(new ClickableStackNothing(BoosterConfig.getNoBoostersIcon()), 13, inv);
		});
	}

	private ClickableStackLinkedMenu getBoosterStack(Booster b) {
		ClickableStackLinkedMenu stack = new ClickableStackLinkedMenu(b.getStack().getType(),
				ChatColor.RESET + "" + ChatColor.GREEN + b.getName(), b.getStack().getItemMeta().getLore(),
				new BoostersGuiConfirm(player, b, this));

		if (b.getStack().hasItemMeta() && b.getStack().getItemMeta() instanceof Damageable)
			ItemUtils.setDamage(stack.getStack(), ((Damageable) b.getStack().getItemMeta()).getDamage());

		return stack;
	}

	@Override
	public void open(Player player) {
		if (player.equals(this.player))
			player.openInventory(inv);
		else
			throw new IllegalStateException("Player for GuiMenu doesn't match menu.open(player)");
	}

}
