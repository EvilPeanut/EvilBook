package com.amentrix.evilbook.nametag;

class NametagCommand
{
	static void setNametagHard(String player, String prefix, String suffix)
	{
		NametagManager.overlap(player, prefix, suffix);
	}

	static void setNametagSoft(String player, String prefix, String suffix)
	{
		NametagManager.update(player, prefix, suffix);
	}
}