package me.vikame.gems.helper;

import java.util.List;
import java.util.stream.Collectors;
import me.vikame.gems.Gems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigHelper {

  /**
   * Parses an {@link ItemStack} from the given {@link ConfigurationSection}
   *
   * @param section the section to parse an {@link ItemStack} from
   * @return a parsed item stack
   */
  public static ItemStack parseItem(ConfigurationSection section) {
    // Get the Material for the parsed item.
    String materialString = section.getString("material");
    if (materialString == null) {
      Gems.getInstance().getLogger().warning("No 'material' key found.");
      return null;
    }

    // Make sure the Material is a valid Bukkit Material.
    Material material = Material.getMaterial(materialString);
    if (material == null) {
      Gems.getInstance().getLogger()
          .warning("Failed to parse material " + materialString + " to a valid Material.");
      return null;
    }

    // Get stack amount for the item, defaulting to 1 if it does not exist.
    int amount = section.getInt("amount", 1);

    ItemStack item = new ItemStack(material, amount);

    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      Gems.getInstance().getLogger().warning("ItemStack does not support ItemMeta.");
      return null;
    }

    // Get the name of the item, and translate color codes.
    String name = section.getString("name");
    if (name != null) {
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
    }

    // Get the lore of the item, and translate color codes for each line.
    List<String> lore = section.getStringList("lore");
    if (!lore.isEmpty()) {
      meta.setLore(
          lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(
              Collectors.toList()));
    }

    // Add an enchantment effect on the item.
    if (section.getBoolean("enchanted", false)) {
      meta.addEnchant(Enchantment.DURABILITY, 1, true);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    item.setItemMeta(meta);
    return item;
  }

}
