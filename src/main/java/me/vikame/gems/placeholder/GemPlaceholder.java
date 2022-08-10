package me.vikame.gems.placeholder;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.vikame.gems.GemHolder;
import me.vikame.gems.Gems;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemPlaceholder extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {
    return "barngems";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Vikame";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public @NotNull List<String> getPlaceholders() {
    return Arrays.asList(
        "%barngems_balance%",
        "%barngems_spent%",
        "%barngems_earned%"
    );
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    GemHolder holder = Gems.getInstance().getHolderCache().getGemHolder(player.getUniqueId());

    if (holder == null) {
      return "Loading...";
    } else {
      int number;
      if (params.equalsIgnoreCase("balance")) { // %barngems_balance%
        number = holder.getBalance();
      } else if (params.equalsIgnoreCase("spent")) { // %barngems_spent%
        number = holder.getTotalGemsSpent();
      } else if (params.equalsIgnoreCase("earned")) { // %barngems_earned%
        number = holder.getTotalGemsEarned();
      } else {
        return null;
      }

      return NumberFormat.getInstance().format(number);
    }
  }
}
