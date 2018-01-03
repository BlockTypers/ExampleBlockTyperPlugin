package com.blocktyper.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ExampleCommand implements CommandExecutor {

	public static String EXAMPLE_COMMAND = "example";

	private ExamplePlugin plugin;

	public ExampleCommand(ExamplePlugin plugin) {
		this.plugin = plugin;
		plugin.getCommand(EXAMPLE_COMMAND).setExecutor(this);
		plugin.debugInfo("'/" + EXAMPLE_COMMAND + "' registered");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (!player.isOp())
			return false;

		player.sendMessage("You must be privileged");

		return true;

	}

}
