package me.vikame.gems.shop;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import me.vikame.gems.GemHolder;
import me.vikame.gems.Gems;
import me.vikame.gems.ui.UI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopUI extends UI {

  private final GemHolder holder;
  private final Shop shop;

  public ShopUI(GemHolder holder) {
    super("Gem Shop (" + NumberFormat.getInstance().format(holder.getBalance()) + " Gems)", 27);

    this.holder = holder;

    this.shop = Gems.getInstance().getShop();

    // Insert all ShopCategory display items into the Inventory, with some added metadata modifications.
    for (ShopCategory category : shop.getCategories()) {
      ItemStack item = category.getDisplayItem().clone();
      ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

      List<String> lore;

      // If we have lore, add a spacer. Otherwise, ensure we have a lore list to add to.
      if (meta.hasLore()) {
        lore = new LinkedList<>(Objects.requireNonNull(meta.getLore()));
        lore.add(ChatColor.GRAY.toString());
      } else {
        lore = new LinkedList<>();
      }

      lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to view.");

      // Update our copy of the inventory item.
      meta.setLore(lore);
      item.setItemMeta(meta);

      this.inventory.setItem(category.getSlot(), item);
    }
  }

  @Override
  public void onClick(Player player, InventoryClickEvent event) {
    // Find the ShopCategory matching the slot the player clicked.
    ShopCategory selectedCategory = shop.getCategoryBySlot(event.getSlot());
    if (selectedCategory == null) {
      return;
    }

    // Open an inventory with the items available in the clicked ShopCategory.
    player.openInventory(new CategoryUI(holder, selectedCategory).getInventory());
  }
}
