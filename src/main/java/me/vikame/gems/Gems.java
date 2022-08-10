package me.vikame.gems;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import me.vikame.gems.commands.GemShopCommand;
import me.vikame.gems.commands.GemsCommand;
import me.vikame.gems.listener.PlayerListener;
import me.vikame.gems.placeholder.GemPlaceholder;
import me.vikame.gems.shop.Shop;
import me.vikame.gems.ui.UIListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Gems extends JavaPlugin {

  private static Gems instance;

  private HolderCache holderCache;
  private Shop shop;

  public static Gems getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;

    // Make sure our data folder and config.yml files exist.
    if (!getDataFolder().exists()) {
      //noinspection ResultOfMethodCallIgnored
      getDataFolder().mkdirs();
    }

    saveDefaultConfig();

    // Member fields.
    this.holderCache = new HolderCache();
    this.shop = new Shop();

    // Register listeners.
    Bukkit.getPluginManager().registerEvents(new UIListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

    // Register commands.
    Objects.requireNonNull(getCommand("gems")).setExecutor(new GemsCommand());
    Objects.requireNonNull(getCommand("gemshop")).setExecutor(new GemShopCommand());

    // Check for PlaceholderAPI, and register our hook if it exists.
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new GemPlaceholder().register();
    }

    // Load GemHolders for all online players in the case of a reload.
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      holderCache.loadGemHolder(player.getUniqueId());
    }

    // Write data to disk every minute (1200 ticks).
    new BukkitRunnable() {
      public void run() {
        try {
          holderCache.save();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.runTaskTimerAsynchronously(this, 20 * 60, 20 * 60);
  }

  @Override
  public void onDisable() {
    // Unload GemHolders for all online players before writing data to disk.
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      try {
        this.holderCache.unloadGemHolder(player.getUniqueId()).get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    // Write data to disk.
    try {
      this.holderCache.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the {@link HolderCache} containing all loaded {@link GemHolder}s
   *
   * @return a {@link HolderCache}
   */
  public HolderCache getHolderCache() {
    return holderCache;
  }

  /**
   * Get the {@link Shop} instance
   *
   * @return a {@link Shop}
   */
  public Shop getShop() {
    return shop;
  }
}
