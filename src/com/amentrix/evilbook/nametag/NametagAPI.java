package com.amentrix.evilbook.nametag;

public class NametagAPI
{
	public static void setPrefix(final String player, final String prefix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.update(player, prefix, null);
			}
		});
	}

	public static void setSuffix(final String player, final String suffix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.update(player, null, suffix);
			}
		});
	}

	public static void setNametagHard(final String player, final String prefix, final String suffix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.overlap(player, prefix, suffix);
			}
		});
	}

	public static void setNametagSoft(final String player, final String prefix, final String suffix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.update(player, prefix, suffix);
			}
		});
	}

	public static void updateNametagHard(final String player, final String prefix, final String suffix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.overlap(player, prefix, suffix);
			}
		});
	}

	public static void updateNametagSoft(final String player, final String prefix, final String suffix)
	{
		NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.update(player, prefix, suffix);
			}
		});
	}

	public static String getPrefix(final String player)
	{
		return NametagManager.getPrefix(player);
	}

	public static String getSuffix(final String player)
	{
		return NametagManager.getSuffix(player);
	}

	public static String getNametag(final String player)
	{
		return NametagManager.getFormattedName(player);
	}
}