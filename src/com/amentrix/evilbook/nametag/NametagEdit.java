package com.amentrix.evilbook.nametag;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.amentrix.evilbook.main.EvilBook;

public class NametagEdit
{
	static LinkedHashMap<String, LinkedHashMap<String, String>> groups = null;
	public static String name = "";
	public static String type = "";
	public static String version = "";
	public static String link = "";
	public static EvilBook plugin;

	public static void load(EvilBook plugin)
	{
		NametagEdit.plugin = plugin;
		NametagManager.load();
		plugin.getServer().getPluginManager().registerEvents(new NametagEventHandler(), plugin);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				Player[] onlinePlayers = Bukkit.getOnlinePlayers();

				for (Player p : onlinePlayers)
				{
					NametagManager.clear(p.getName());
				}
			}
		});
	}
}