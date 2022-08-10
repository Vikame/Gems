package me.vikame.gems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class GemHolder {

  private final UUID uuid;
  private final Map<String, Integer> purchaseCounts;
  private int totalGemsEarned;
  private int currentBalance;

  /**
   * Create a new {@link GemHolder} corresponding to a {@link org.bukkit.entity.Player} with the
   * given uuid.
   *
   * @param uuid the UUID of the corresponding {@link org.bukkit.entity.Player}
   */
  public GemHolder(UUID uuid) {
    this.uuid = uuid;
    this.currentBalance = 0;
    this.totalGemsEarned = 0;
    this.purchaseCounts = new HashMap<>();
  }

  /**
   * Load a pre-existing {@link GemHolder} from the provided {@link ConfigurationSection}.
   *
   * @param uuid    the UUID of the corresponding {@link org.bukkit.entity.Player}
   * @param section the section to load data from
   */
  public GemHolder(UUID uuid, ConfigurationSection section) {
    this.uuid = uuid;

    this.totalGemsEarned = section.getInt("totalGemsEarned");
    this.currentBalance = section.getInt("currentBalance");

    this.purchaseCounts = new HashMap<>();

    ConfigurationSection purchasedSection = section.getConfigurationSection("purchaseCounts");
    if (purchasedSection != null) {
      for (String key : purchasedSection.getKeys(false)) {
        int amount = purchasedSection.getInt(key);

        purchaseCounts.put(key, amount);
      }
    }
  }

  /**
   * Load a pre-existing {@link GemHolder} corresponding to a {@link org.bukkit.entity.Player} with
   * the given uuid, cumulative total of gems earned, current balance, and total counts of purchased
   * items.
   *
   * @param uuid            the UUID of the corresponding {@link org.bukkit.entity.Player}
   * @param totalGemsEarned the cumulative total of gems earned
   * @param balance         the current gem balance
   */
  public GemHolder(UUID uuid, int totalGemsEarned, int balance,
      Map<String, Integer> purchaseCounts) {
    this.uuid = uuid;
    this.totalGemsEarned = totalGemsEarned;
    this.currentBalance = balance;
    this.purchaseCounts = purchaseCounts;
  }

  /**
   * Gets the UUID of the {@link org.bukkit.entity.Player} that this {@link GemHolder} corresponds
   * to.
   *
   * @return player uuid
   */
  public UUID getUUID() {
    return uuid;
  }

  /**
   * Gets the cumulative total number of gems earned by this {@link GemHolder}
   *
   * @return the total number of gems earned
   */
  public int getTotalGemsEarned() {
    return totalGemsEarned;
  }

  /**
   * Sets the cumulative number of all gems earned by this {@link GemHolder}, without doing any
   * validation. link
   *
   * @param totalGemsEarned the cumulative total of gems earned
   */
  public void setTotalGemsEarned(int totalGemsEarned) {
    this.totalGemsEarned = totalGemsEarned;
  }

  /**
   * Gets the total number of gems this {@link GemHolder} has used.
   *
   * @return the total number of spent gems
   */
  public int getTotalGemsSpent() {
    return totalGemsEarned - currentBalance;
  }

  /**
   * Gets the total number of unspent gems available to this {@link GemHolder}.
   *
   * @return the available gem balance
   */
  public int getBalance() {
    return currentBalance;
  }

  /**
   * Sets the total number of gems available to this {@link GemHolder}, without updating cumulative
   * gems earned.
   *
   * @param balance the total number of gems
   */
  public void setBalance(int balance) {
    this.currentBalance = balance;
  }

  /**
   * Gives this {@link GemHolder} additional gems, and updates the cumulative total gems earned.
   *
   * @param gems the number of gems to give
   */
  public void giveGems(int gems) {
    if (gems < 0) {
      throw new IllegalArgumentException("invalid gem amount: cannot give less than 0 gems");
    }

    // Check for integer overflow on current balance.
    if (this.currentBalance + gems < this.currentBalance) {
      this.currentBalance = Integer.MAX_VALUE;
    } else {
      this.currentBalance += gems;
    }

    // Check for integer overflow on cumulative total.
    if (this.totalGemsEarned + gems < this.totalGemsEarned) {
      this.totalGemsEarned = Integer.MAX_VALUE;
    } else {
      this.totalGemsEarned += gems;
    }
  }

  /**
   * Takes gems away from the {@link GemHolder}.
   *
   * @param gems the number of gems to take
   */
  public void takeGems(int gems) {
    if (gems < 0) {
      throw new IllegalArgumentException("invalid gem amount: cannot take less than 0 gems.");
    }

    if (this.currentBalance < gems) {
      Gems.getInstance().getLogger()
          .warning(String.format("Attempted to take %d gems from %s, but they only had %d gems.",
              gems, uuid.toString(), currentBalance));

      this.currentBalance = 0;
    } else {
      this.currentBalance -= gems;
    }
  }

  /**
   * Gets all purchased items and the number of times they have been purchased by this {@link
   * GemHolder}.
   *
   * @return a map of purchased items and counts
   */
  public Map<String, Integer> getPurchaseCounts() {
    return purchaseCounts;
  }

  /**
   * Adds to this {@link GemHolder}'s purchase count of the corresponding item.
   *
   * @param itemId the purchased item id
   */
  public void addPurchase(String itemId) {
    purchaseCounts.compute(itemId, ((key, value) -> {
      if (value == null) {
        return 1;
      } else {
        return value + 1;
      }
    }));
  }

  /**
   * Gets the number of times this {@link GemHolder} has purchased the corresponding item.
   *
   * @param itemId the item id to get the purchase count of
   * @return the number of times the item has been purchased
   */
  public int getPurchaseCount(String itemId) {
    return purchaseCounts.getOrDefault(itemId, 0);
  }

  /**
   * Writes the data for this {@link GemHolder} to a {@link YamlConfiguration}
   *
   * @param config the config to write data to
   */
  public void writeToConfig(YamlConfiguration config) {
    ConfigurationSection section = config.createSection(uuid.toString());
    section.set("totalGemsEarned", totalGemsEarned);
    section.set("currentBalance", currentBalance);

    section.createSection("purchasedItems", purchaseCounts);
  }
}
