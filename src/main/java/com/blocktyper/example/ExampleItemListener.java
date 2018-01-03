package com.blocktyper.example;

import com.blocktyper.v1_2_6.nbt.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleItemListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        try {
            ItemStack itemInHand = event.getItemInHand();

            // if player is not holding a item, do not continue
            if (itemInHand == null) {
                ExamplePlugin.getPlugin().debugInfo("Not holding an item");
                return;
            }
            NBTItem nbtItem = new com.blocktyper.v1_2_6.nbt.NBTItem(itemInHand);
            String nbtRecipeKey = nbtItem.getString(ExamplePlugin.RECIPES_KEY);
            if (nbtRecipeKey == null) {
                ExamplePlugin.getPlugin().debugInfo("Not holding an example item.'");
                return;
            }

            event.getPlayer().sendMessage("You cant place an example item!");
            event.setCancelled(true);

        } catch (Exception e) {
            ExamplePlugin.getPlugin()
                    .warning("Unexpected error in 'ExampleItemListener.onBlockPlace'. Message: " + e.getMessage());
        }

    }
}
