package com.amentrix.evilbook.invent;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

/*
 * Tweak to prevent hatch (Trap Door) physics
 * which prevents hatchs from breaking when supporting block isn't present
 */
public class HatchTweak implements Listener {
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		Block block = event.getBlock();
		
		if(block.getType() == Material.TRAP_DOOR) {
			event.setCancelled(true);
		}
	}
}
