package me.vikame.gems.commands;

import me.vikame.gems.GemHolder;
import me.vikame.gems.Gems;
import me.vikame.gems.HolderCache;
import me.vikame.gems.shop.ShopUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GemShopCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Only a player can execute this command.");
      return true;
    }

    Gems plugin = Gems.getInstance();
    HolderCache cache = plugin.getHolderCache();

    Player player = (Player) sender;

    GemHolder holder = cache.getGemHolder(player.getUniqueId());
    if (holder == null) {
      sender.sendMessage(ChatColor.RED + "Please wait a moment while your gem data is loaded...");
      return true;
    }

    player.openInventory(new ShopUI(holder).getInventory());
    return true;
  }
}
