package me.vikame.gems.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class UIListener implements Listener {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getWhoClicked();

    Inventory top = player.getOpenInventory().getTopInventory();

    // If the top inventory is a UI instance, we don't want to allow shift-clicking of items into the UI inventory.
    if (top.getHolder() instanceof UI) {
      event.setCancelled(true);
    }

    Inventory inventory = event.getClickedInventory();
    if (inventory == null) {
      return;
    }

    // If the clicked inventory is a UI instance, we delegate to UI#onClick()
    if (inventory.getHolder() instanceof UI) {
      event.setCancelled(true);
      ((UI) inventory.getHolder()).onClick(player, event);
    }
  }

}
