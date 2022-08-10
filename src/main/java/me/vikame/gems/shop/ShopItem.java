package me.vikame.gems.shop;

import java.util.List;
import java.util.Objects;
import me.vikame.gems.helper.ConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

  private final String itemId;
  private final String category;
  private final int slot;
  private final int cost;
  private final int maxPurchases;
  private final List<String> giveCommands;
  private final String purchaseMessage;
  private final ItemStack displayItem;

  public ShopItem(ConfigurationSection section) {
    this.itemId = section.getName();
    this.category = section.getString("category");
    this.slot = section.getInt("slot");
    this.cost = section.getInt("cost");
    this.maxPurchases = section.getInt("max_purchases", -1);
    this.giveCommands = section.getStringList("give_commands");
    this.purchaseMessage = section.getString("purchase_message");
    this.displayItem = ConfigHelper.parseItem(
        Objects.requireNonNull(section.getConfigurationSection("display_item")));
  }

  /**
   * Get the item id of this {@link ShopItem}.
   *
   * @return the {@link ShopItem}s internal item id
   */
  public String getItemId() {
    return itemId;
  }

  /**
   * Get the internal id of the {@link ShopCategory} this {@link ShopItem} corresponds to.
   *
   * @return the {@link ShopCategory}s internal category id
   */
  public String getCategory() {
    return category;
  }

  /**
   * Get the slot the {@link ShopItem#getDisplayItem()} should be displayed in.
   *
   * @return the slot the display item should appear in
   */
  public int getSlot() {
    return slot;
  }

  /**
   * Get the gem cost of this {@link ShopItem}
   *
   * @return cost of this {@link ShopItem}
   */
  public int getCost() {
    return cost;
  }

  /**
   * Get the maximum number of purchases this {@link ShopItem} allows.
   *
   * @return the maximum number of times this {@link ShopItem} can be purchased
   */
  public int getMaxPurchases() {
    return maxPurchases;
  }

  /**
   * Get the commands to run when this {@link ShopItem} is purchased.
   *
   * @return the commands to run when this {@link ShopItem} is purchased
   */
  public List<String> getGiveCommands() {
    return giveCommands;
  }

  /**
   * Get the message to send the buyer of this {@link ShopItem} when it is purchased.
   *
   * @return the purchase message to send a buyer
   */
  public String getPurchaseMessage() {
    return purchaseMessage;
  }

  /**
   * Get the item to display in the {@link CategoryUI} representing this {@link ShopItem}
   *
   * @return a display item representing this {@link ShopItem}
   */
  public ItemStack getDisplayItem() {
    return displayItem;
  }

}
