package me.vikame.gems.listener;

import me.vikame.gems.Gems;
import me.vikame.gems.HolderCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    Gems plugin = Gems.getInstance();
    HolderCache cache = plugin.getHolderCache();

    // Load the GemHolder
    cache.loadGemHolder(player.getUniqueId())
        .thenAccept(holder -> {
          plugin.getLogger().info("Loaded GemHolder for player " + player.getUniqueId());
        });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    Gems plugin = Gems.getInstance();
    HolderCache cache = plugin.getHolderCache();

    // Unload the GemHolder
    cache.unloadGemHolder(player.getUniqueId())
        .thenAccept(holder -> {
          plugin.getLogger().info("Unloaded GemHolder for player " + player.getUniqueId());
        });
  }

}
