package com.amentrix.evilbook.nametag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

class NametagEventHandler
implements Listener
{
	@EventHandler(priority=EventPriority.HIGHEST)
	void onPlayerJoin(PlayerJoinEvent e)
	{
		NametagManager.sendTeamsToPlayer(e.getPlayer());
		NametagManager.clear(e.getPlayer().getName());
	}
}