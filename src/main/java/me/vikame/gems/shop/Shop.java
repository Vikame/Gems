package me.vikame.gems.shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.vikame.gems.Gems;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Shop {

  private final Map<String, ShopCategory> categories;
  private final Map<Integer, ShopCategory> categoriesBySlot;

  public Shop() {
    this.categories = new HashMap<>();
    this.categoriesBySlot = new HashMap<>();

    FileConfiguration config = Gems.getInstance().getConfig();

    ConfigurationSection categorySection = config.getConfigurationSection("categories");

    // Load the defined categories from the configuration file.
    for (String categoryId : Objects.requireNonNull(categorySection).getKeys(false)) {
      ShopCategory category = new ShopCategory(
          Objects.requireNonNull(categorySection.getConfigurationSection(categoryId)));

      categories.put(categoryId.toLowerCase(), category);
      categoriesBySlot.put(category.getSlot(), category);
    }

    ConfigurationSection itemSection = config.getConfigurationSection("items");

    // Load the defined items from the configuration file.
    for (String itemId : Objects.requireNonNull(itemSection).getKeys(false)) {
      ShopItem item = new ShopItem(
          Objects.requireNonNull(itemSection.getConfigurationSection(itemId)));

      ShopCategory category = categories.get(item.getCategory().toLowerCase());
      if (category == null) {
        Gems.getInstance().getLogger()
            .warning("Could not find shop category '" + item.getCategory() + "'");
        continue;
      }

      category.getItems().put(item.getSlot(), item);
    }
  }

  /**
   * Get all {@link ShopCategory}s
   *
   * @return a collection of all {@link ShopCategory}s
   */
  public Collection<ShopCategory> getCategories() {
    return categories.values();
  }

  /**
   * Retrieves a {@link ShopCategory} corresponding to the given internal id
   *
   * @param id the internal id of the wanted category
   * @return a shop category corresponding to the internal id, or null
   */
  public ShopCategory getCategoryById(String id) {
    return categories.get(id);
  }

  /**
   * Retrieves a {@link ShopCategory} corresponding to the given inventory slot
   *
   * @param slot the inventory slot of the wanted category
   * @return a shop category corresponding to the inventory slot, or null
   */
  public ShopCategory getCategoryBySlot(int slot) {
    return categoriesBySlot.get(slot);
  }

}
