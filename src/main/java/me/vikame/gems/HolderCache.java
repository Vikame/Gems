package me.vikame.gems;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class HolderCache {

  private final File holderFile;
  private final ReentrantReadWriteLock rwLock;
  private final YamlConfiguration config;
  private final Map<UUID, GemHolder> holders;

  /**
   * Create a new {@link HolderCache}
   */
  public HolderCache() {
    this.holderFile = new File(Gems.getInstance().getDataFolder(), "holders.yml");

    this.rwLock = new ReentrantReadWriteLock();
    this.config = YamlConfiguration.loadConfiguration(holderFile);

    this.holders = new ConcurrentHashMap<>();
  }

  /**
   * Returns a {@link CompletableFuture} that will load a given {@link GemHolder} from the config.
   *
   * @param uuid the UUID of the corresponding player
   * @return a {@link CompletableFuture} loading the {@link GemHolder}
   */
  public CompletableFuture<GemHolder> loadGemHolder(UUID uuid) {
    GemHolder existingHolder = holders.get(uuid);

    // If a cached GemHolder already existed with the UUID, we can just return it.
    if (existingHolder != null) {
      return CompletableFuture.completedFuture(existingHolder);
    }

    return CompletableFuture.supplyAsync(() -> {
      // Since we're reading YamlConfiguration data, we need to obtain a read-lock here.
      ReadLock lock = rwLock.readLock();
      lock.lock();

      GemHolder toReturn;

      ConfigurationSection section = config.getConfigurationSection(uuid.toString());

      if (section != null) {
        // If the UUID has pre-existing data, we load that from the YamlConfiguration.
        toReturn = new GemHolder(uuid, section);
      } else {
        // If the UUID does not have any pre-existing data, no loading needs to be done.
        toReturn = new GemHolder(uuid);
      }

      // Remember to release the write-lock. The lock is no longer necessary after this point as we no longer interact with the YamlConfiguration.
      lock.unlock();

      // Cache our new GemHolder.
      this.holders.put(uuid, toReturn);

      return toReturn;
    });
  }

  /**
   * Removes a {@link GemHolder} from the cache, and returns a {@link CompletableFuture} that
   * completes when all {@link YamlConfiguration} operations are completed.
   *
   * @param uuid the UUID of the corresponding player
   * @return a {@link CompletableFuture} that completes when config operations are complete
   */
  public CompletableFuture<Void> unloadGemHolder(UUID uuid) {
    GemHolder holder = holders.remove(uuid);

    // If we weren't able to remove a GemHolder from the cache we take a fast-path, outputting a warning.
    if (holder == null) {
      Gems.getInstance().getLogger()
          .warning("Attempted to unload non-existent gem holder with id " + uuid.toString());
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.supplyAsync(() -> {
      // Since we're writing YamlConfiguration data, we need to obtain a write-lock here.
      WriteLock lock = rwLock.writeLock();
      lock.lock();

      // Write user data to the cached config. This does *not* write to disk until HolderCache#save() is called.
      holder.writeToConfig(config);

      // Remember to release the write-lock.
      lock.unlock();
      return null;
    });
  }

  /**
   * Retrieves a cached {@link GemHolder} with the corresponding {@link UUID}
   *
   * @param uuid the UUID of the corresponding player
   * @return a {@link GemHolder} corresponding to the {@link UUID}
   */
  public GemHolder getGemHolder(UUID uuid) {
    return holders.get(uuid);
  }

  /**
   * Saves the cached {@link YamlConfiguration} data to disk.
   *
   * @throws IOException if saving to file was unsuccessful
   */
  public void save() throws IOException {
    // Since YamlConfiguration#save() has to read cached config data, we need to obtain a read-lock here.
    ReadLock lock = rwLock.readLock();
    lock.lock();

    // Save the cached config data to the holders.yml file.
    config.save(holderFile);

    // Remember to release the read lock.
    lock.unlock();
  }

}
