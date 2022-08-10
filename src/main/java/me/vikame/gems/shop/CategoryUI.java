package me.vikame.gems.shop;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import me.vikame.gems.GemHolder;
import me.vikame.gems.ui.UI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CategoryUI extends UI {

  private final GemHolder holder;
  private final ShopCategory category;

  public CategoryUI(GemHolder holder, ShopCategory category) {
    super("Gem Shop (" + NumberFormat.getInstance().format(holder.getBalance()) + " Gems)", 27);
    this.holder = holder;

    this.category = category;

    // Insert all ShopItem display items into the Inventory, with some added metadata modifications.
    for (ShopItem shopItem : category.getItems().values()) {
      ItemStack item = shopItem.getDisplayItem().clone();
      ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

      List<String> lore;

      // If we have lore, add a spacer. Otherwise, ensure we have a lore list to add to.
      if (meta.hasLore()) {
        lore = new LinkedList<>(Objects.requireNonNull(meta.getLore()));
        lore.add(ChatColor.GRAY.toString());
      } else {
        lore = new LinkedList<>();
      }

      // Whether to show the price of the item at the bottom of the lore list.
      boolean showPrice = true;

      // Add purchase counts to the lore list if the ShopItem has a defined number of max purchases.
      if (shopItem.getMaxPurchases() != -1) {
        int purchaseCount = holder.getPurchaseCount(shopItem.getItemId());

        if (purchaseCount >= shopItem.getMaxPurchases()) {
          lore.add(ChatColor.RED + "Already purchased.");
          showPrice = false;
        } else {
          lore.add(ChatColor.GRAY + "Purchased "
              + NumberFormat.getInstance().format(purchaseCount) + "/" + NumberFormat.getInstance()
              .format(shopItem.getMaxPurchases())
              + " times.");
        }
      }

      if (showPrice) {
        // Show the price of the item.
        lore.add(
            (holder.getBalance() >= shopItem.getCost() ? ChatColor.GREEN : ChatColor.RED) + "Cost: "
                + ChatColor.WHITE + shopItem.getCost());
      }

      meta.setLore(lore);
      item.setItemMeta(meta);

      this.inventory.setItem(shopItem.getSlot(), item);
    }
  }

  @Override
  public void onClick(Player player, InventoryClickEvent event) {
    int slot = event.getSlot();

    // Find the ShopItem matching the slot the player clicked.
    ShopItem item = category.getItems().get(slot);
    if (item == null) {
      return;
    }

    // Check if the GemHolder has a sufficient gem balance.
    if (holder.getBalance() < item.getCost()) {
      return;
    }

    // Check if the GemHolder has already bought the maximum number of the ShopItem.
    if (item.getMaxPurchases() != -1) {
      if (holder.getPurchaseCount(item.getItemId()) >= item.getMaxPurchases()) {
        if (item.getMaxPurchases() == 1) {
          player.sendMessage(ChatColor.RED + "You have already purchased this item.");
        } else {
          player.sendMessage(
              ChatColor.RED + "You have already purchased all " + item.getMaxPurchases()
                  + " of this item.");
        }

        return;
      }
    }

    player.closeInventory();

    // Take the gems from the GemHolder, and add to their purchase count of this time.
    holder.takeGems(item.getCost());
    holder.addPurchase(item.getItemId());

    // Dispatch the item "give commands" via console.
    for (String command : item.getGiveCommands()) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          command.replace("%player%", player.getName()));
    }

    // Send the player a message to confirm that the purchase was successful.
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', item.getPurchaseMessage()
        .replace("%purchased_item%", item.getItemId())
        .replace("%purchase_count%",
            NumberFormat.getInstance().format(holder.getPurchaseCount(item.getItemId())))
        .replace("%max_purchases%", NumberFormat.getInstance().format(item.getMaxPurchases())))
    );
  }
}
