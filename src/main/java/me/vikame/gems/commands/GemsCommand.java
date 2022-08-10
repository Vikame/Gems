package me.vikame.gems.commands;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.vikame.gems.GemHolder;
import me.vikame.gems.Gems;
import me.vikame.gems.HolderCache;
import me.vikame.gems.shop.ShopUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GemsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Gems plugin = Gems.getInstance();
    HolderCache cache = plugin.getHolderCache();

    if (args.length == 0) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(
            ChatColor.RED + "Usage: /" + label + " <set | give | take> <player> <amount>");
        return true;
      }

      Player player = (Player) sender;

      GemHolder holder = cache.getGemHolder(player.getUniqueId());
      if (holder == null) {
        sender.sendMessage(ChatColor.RED + "Please wait a moment while your gem data is loaded...");
        return true;
      }

      player.sendMessage(ChatColor.GRAY + "Gem Balance: " + NumberFormat.getInstance()
          .format(holder.getBalance()));
      return true;
    }

    String subcommand = args[0];
    if (subcommand.equalsIgnoreCase("shop")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(
            ChatColor.RED + "Usage: /" + label + " <set | give | take> <player> <amount>");
        return true;
      }

      Player player = (Player) sender;

      GemHolder holder = cache.getGemHolder(player.getUniqueId());
      if (holder == null) {
        sender.sendMessage(ChatColor.RED + "Please wait a moment while your gem data is loaded...");
        return true;
      }

      player.openInventory(new ShopUI(holder).getInventory());
      return true;
    }

    if (args.length < 3) {
      sender.sendMessage(
          ChatColor.RED + "Usage: /" + label + " <set | give | take> <player> <amount>");
      return true;
    }

    OfflinePlayer commandTarget = Bukkit.getOfflinePlayer(args[1]);
    if (!commandTarget.hasPlayedBefore()) {
      sender.sendMessage(ChatColor.RED + "The player " + args[1] + " has not played before.");
      return true;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number.");
      return true;
    }

    if (amount < 0) {
      sender.sendMessage(ChatColor.RED + "Amount cannot be negative.");
      return true;
    }

    String targetName = commandTarget.getName();
    UUID targetUuid = commandTarget.getUniqueId();

    CompletableFuture<GemHolder> futureHolder = cache.loadGemHolder(targetUuid);

    Consumer<GemHolder> action = null;
    if (subcommand.equalsIgnoreCase("set")) {
      action = holder -> {
        holder.setBalance(amount);
        sender.sendMessage(
            ChatColor.GRAY + "Set " + targetName + "'s gem amount to " + amount + ".");
      };
    } else if (subcommand.equalsIgnoreCase("give")) {
      action = holder -> {
        if (amount < 1) {
          sender.sendMessage(ChatColor.RED + "Cannot give less than 1 gem.");
          return;
        }

        holder.giveGems(amount);
        sender.sendMessage(ChatColor.GRAY + "Gave " + amount + " gems to " + targetName + ".");
      };
    } else if (subcommand.equalsIgnoreCase("take")) {
      action = holder -> {
        if (amount < 1) {
          sender.sendMessage(ChatColor.RED + "Cannot take less than 1 gem.");
          return;
        }

        holder.takeGems(amount);
        sender.sendMessage(ChatColor.GRAY + "Took " + amount + " gems from " + targetName + ".");
      };
    }

    if (action == null) {
      sender.sendMessage(ChatColor.RED + "Invalid sub-command.");
      return true;
    }

    futureHolder.thenAccept(action).thenRun(() -> {
      if (commandTarget.isOnline()) {
        return;
      }

      cache.unloadGemHolder(targetUuid);
    });

    return true;
  }

}
