package com.blocktyper.example;

import com.blocktyper.v1_2_6.BlockTyperListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExampleListener extends BlockTyperListener {

	public ExampleListener(ExamplePlugin plugin) {
		super();
		init(plugin);
		register();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerJoin(PlayerInteractEvent event) {
		event.getPlayer().sendMessage("Example -  [" + (event.getClickedBlock() != null ? event.getClickedBlock().getType() : "") + "]");
	}

}
