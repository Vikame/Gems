package me.vikame.gems.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class UI implements InventoryHolder {

  private final int size;
  protected Inventory inventory;
  private String name;

  /**
   * Creates a {@link UI} with the given name and size.
   *
   * @param name the name of the {@link UI}
   * @param size the intended size of the {@link UI}
   */
  public UI(String name, int size) {
    this.name = name;
    this.size = size;
    this.inventory = Bukkit.createInventory(this, size, name);
  }

  /**
   * Called when a {@link InventoryClickEvent} is fired in relation to the underlying {@link
   * Inventory}
   *
   * @param player the player who clicked the {@link UI}
   * @param event  the click event on the underlying {@link Inventory}
   */
  public abstract void onClick(Player player, InventoryClickEvent event);

  /**
   * Get the name of the {@link UI} that is used as the underlying {@link Inventory}'s title
   *
   * @return the name of the {@link UI}
   */
  public String getName() {
    return name;
  }

  /**
   * Changes the name of the {@link UI}, recreating the underlying {@link Inventory} and re-opening
   * the {@link Inventory} for existing viewers.
   *
   * @param name the new name of the {@link UI}
   */
  public void setName(String name) {
    this.name = name;

    Inventory oldInv = this.inventory;

    // Re-create the inventory with the new name.
    this.inventory = Bukkit.createInventory(this, size, name);
    this.inventory.setContents(oldInv.getContents());

    // Re-open the inventory for all players who were previously viewing the inventory.
    for (HumanEntity ent : oldInv.getViewers()) {
      ent.openInventory(inventory);
    }
  }

  /**
   * Get the intended size of the {@link UI}
   *
   * @return the size of the {@link UI}
   */
  public int getSize() {
    return size;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }
}
