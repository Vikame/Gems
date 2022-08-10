package me.vikame.gems.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.vikame.gems.helper.ConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ShopCategory {

  private final String categoryId;
  private final int slot;
  private final ItemStack displayItem;

  private final Map<Integer, ShopItem> items;

  public ShopCategory(ConfigurationSection section) {
    this.categoryId = section.getName();
    this.slot = section.getInt("slot");
    this.displayItem = ConfigHelper.parseItem(
        Objects.requireNonNull(section.getConfigurationSection("display_item")));

    this.items = new HashMap<>();
  }

  /**
   * Get the category id of this {@link ShopCategory}.
   *
   * @return the {@link ShopCategory}s internal category id
   */
  public String getCategoryId() {
    return categoryId;
  }

  /**
   * Get the slot the {@link ShopCategory#getDisplayItem()} should be displayed in.
   *
   * @return the slot the display item should appear in
   */
  public int getSlot() {
    return slot;
  }

  /**
   * Get the item to display in the {@link ShopUI} representing this {@link ShopCategory}
   *
   * @return a display item representing this {@link ShopCategory}
   */
  public ItemStack getDisplayItem() {
    return displayItem;
  }

  /**
   * Get all {@link ShopItem}s within this {@link ShopCategory}
   *
   * @return the items within this category
   */
  public Map<Integer, ShopItem> getItems() {
    return items;
  }
}
