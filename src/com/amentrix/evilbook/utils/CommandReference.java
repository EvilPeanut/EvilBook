package com.amentrix.evilbook.utils;

import java.util.HashMap;
import java.util.Map;

import com.amentrix.evilbook.main.Rank;

public class CommandReference {
	public static final Map<String, Rank> commandBlacklist = new HashMap<>();
	
	static {
		commandBlacklist.put("/worldborder", Rank.SERVER_HOST);
		commandBlacklist.put("/replaceitem", Rank.SERVER_HOST);
		commandBlacklist.put("/execute", Rank.SERVER_HOST);
		commandBlacklist.put("/entitydata", Rank.SERVER_HOST);
		commandBlacklist.put("/blockdata", Rank.SERVER_HOST);
		commandBlacklist.put("/clone", Rank.SERVER_HOST);
		commandBlacklist.put("/setblock", Rank.SERVER_HOST);
		commandBlacklist.put("/restart", Rank.SERVER_HOST);
		commandBlacklist.put("/stop", Rank.SERVER_HOST);
		commandBlacklist.put("/op", Rank.SERVER_HOST);
		commandBlacklist.put("/reload", Rank.SERVER_HOST);
		commandBlacklist.put("/makeadmin", Rank.SERVER_HOST);
		commandBlacklist.put("/setspawn", Rank.SERVER_HOST);
		commandBlacklist.put("/xp", Rank.SERVER_HOST);
		commandBlacklist.put("/gamerule", Rank.SERVER_HOST);
		commandBlacklist.put("/deop", Rank.SERVER_HOST);
		commandBlacklist.put("/drug", Rank.SERVER_HOST);
		commandBlacklist.put("/troll", Rank.SERVER_HOST);
		//
		commandBlacklist.put("/demote", Rank.ADMIN_STAFF);
		commandBlacklist.put("/drwatson", Rank.ADMIN_STAFF);
		//
		commandBlacklist.put("/hyperhorse", Rank.TYCOON);
		//
		commandBlacklist.put("/setrank", Rank.INVESTOR);
		//
		commandBlacklist.put("/pardon", Rank.ELITE);
		commandBlacklist.put("/promote", Rank.ELITE);
		//
		commandBlacklist.put("/regen", Rank.COUNCILLOR);
		commandBlacklist.put("//regen", Rank.COUNCILLOR);
		commandBlacklist.put("/copy", Rank.COUNCILLOR);
		commandBlacklist.put("//copy", Rank.COUNCILLOR);
		commandBlacklist.put("/cut", Rank.COUNCILLOR);
		commandBlacklist.put("//cut", Rank.COUNCILLOR);
		commandBlacklist.put("/paste", Rank.COUNCILLOR);
		commandBlacklist.put("//paste", Rank.COUNCILLOR);
		//
		commandBlacklist.put("/deforest", Rank.ADMIN);
		commandBlacklist.put("/testforblocks", Rank.ADMIN);
		commandBlacklist.put("/particle", Rank.ADMIN);
		commandBlacklist.put("/thaw", Rank.ADMIN);
		commandBlacklist.put("/move", Rank.ADMIN);
		commandBlacklist.put("//move", Rank.ADMIN);
		commandBlacklist.put("/snow", Rank.ADMIN);
		commandBlacklist.put("/flip", Rank.ADMIN);
		commandBlacklist.put("//flip", Rank.ADMIN);
		commandBlacklist.put("/sphere", Rank.ADMIN);
		commandBlacklist.put("//sphere", Rank.ADMIN);
		commandBlacklist.put("/hsphere", Rank.ADMIN);
		commandBlacklist.put("//hsphere", Rank.ADMIN);
		commandBlacklist.put("/esphere", Rank.ADMIN);
		commandBlacklist.put("//esphere", Rank.ADMIN);
		commandBlacklist.put("/cspawn", Rank.ADMIN);
		commandBlacklist.put("/spawncreature", Rank.ADMIN);
		commandBlacklist.put("/mob", Rank.ADMIN);
		commandBlacklist.put("/spawnmob", Rank.ADMIN);
		commandBlacklist.put("/forestgen", Rank.ADMIN);
		commandBlacklist.put("/forest", Rank.ADMIN);
		commandBlacklist.put("/pyramid", Rank.ADMIN);
		commandBlacklist.put("//pyramid", Rank.ADMIN);
		commandBlacklist.put("/hpyramid", Rank.ADMIN);
		commandBlacklist.put("//hpyramid", Rank.ADMIN);
		commandBlacklist.put("/hcylinder", Rank.ADMIN);
		commandBlacklist.put("//hcylinder", Rank.ADMIN);
		commandBlacklist.put("/cylinder", Rank.ADMIN);
		commandBlacklist.put("//cylinder", Rank.ADMIN);
		commandBlacklist.put("/hcyl", Rank.ADMIN);
		commandBlacklist.put("//hcyl", Rank.ADMIN);
		commandBlacklist.put("/cyl", Rank.ADMIN);
		commandBlacklist.put("//cyl", Rank.ADMIN);
		commandBlacklist.put("/pumpkins", Rank.ADMIN);
		commandBlacklist.put("/setbiome", Rank.ADMIN);
		commandBlacklist.put("//setbiome", Rank.ADMIN);
		commandBlacklist.put("/count", Rank.ADMIN);
		commandBlacklist.put("//count", Rank.ADMIN);
		commandBlacklist.put("/size", Rank.ADMIN);
		commandBlacklist.put("//size", Rank.ADMIN);
		commandBlacklist.put("/desel", Rank.ADMIN);
		commandBlacklist.put("//desel", Rank.ADMIN);
		commandBlacklist.put("/wand", Rank.ADMIN);
		commandBlacklist.put("//wand", Rank.ADMIN);
		commandBlacklist.put("/drain", Rank.ADMIN);
		commandBlacklist.put("//drain", Rank.ADMIN);
		commandBlacklist.put("/green", Rank.ADMIN);
		commandBlacklist.put("//green", Rank.ADMIN);
		commandBlacklist.put("/overlay", Rank.ADMIN);
		commandBlacklist.put("//overlay", Rank.ADMIN);
		commandBlacklist.put("/walls", Rank.ADMIN);
		commandBlacklist.put("//walls", Rank.ADMIN);
		commandBlacklist.put("/outline", Rank.ADMIN);
		commandBlacklist.put("//outline", Rank.ADMIN);
		commandBlacklist.put("/hollow", Rank.ADMIN);	
		commandBlacklist.put("//hollow", Rank.ADMIN);	
		commandBlacklist.put("/undo", Rank.ADMIN);
		commandBlacklist.put("//undo", Rank.ADMIN);
		commandBlacklist.put("/fill", Rank.ADMIN);
		commandBlacklist.put("//fill", Rank.ADMIN);
		commandBlacklist.put("/set", Rank.ADMIN);
		commandBlacklist.put("//set", Rank.ADMIN);
		commandBlacklist.put("/replace", Rank.ADMIN);
		commandBlacklist.put("//replace", Rank.ADMIN);
		commandBlacklist.put("/rreplace", Rank.ADMIN);
		commandBlacklist.put("/rfill", Rank.ADMIN);
		commandBlacklist.put("/rdel", Rank.ADMIN);
		commandBlacklist.put("/rdelete", Rank.ADMIN);
		commandBlacklist.put("/del", Rank.ADMIN);
		commandBlacklist.put("/delete", Rank.ADMIN);
		commandBlacklist.put("/tree", Rank.ADMIN);
		commandBlacklist.put("/rename", Rank.ADMIN);
		commandBlacklist.put("/nick", Rank.ADMIN);
		commandBlacklist.put("/nickname", Rank.ADMIN);
		commandBlacklist.put("/tool", Rank.ADMIN);
		commandBlacklist.put("/toggleeditwand", Rank.ADMIN);
		//
		commandBlacklist.put("/pos1", Rank.STAFF_DIAMOND);
		commandBlacklist.put("//pos1", Rank.STAFF_DIAMOND);
		commandBlacklist.put("/pos2", Rank.STAFF_DIAMOND);
		commandBlacklist.put("//pos2", Rank.STAFF_DIAMOND);
		commandBlacklist.put("/region", Rank.STAFF_DIAMOND);
		//
		commandBlacklist.put("/vanish", Rank.STAFF_LAPIS);
		commandBlacklist.put("/hide", Rank.STAFF_LAPIS);
		commandBlacklist.put("/unvanish", Rank.STAFF_LAPIS);
		commandBlacklist.put("/show", Rank.STAFF_LAPIS);
		//
		commandBlacklist.put("/tphere", Rank.STAFF_GOLD);
		commandBlacklist.put("/teleporthere", Rank.STAFF_GOLD);
		//
		commandBlacklist.put("/clean", Rank.STAFF_SILVER);
		//
		commandBlacklist.put("/broadcast", Rank.STAFF_COPPER);
		commandBlacklist.put("/say", Rank.STAFF_COPPER);
		commandBlacklist.put("/dawn", Rank.STAFF_COPPER);
		commandBlacklist.put("/day", Rank.STAFF_COPPER);
		commandBlacklist.put("/dusk", Rank.STAFF_COPPER);
		commandBlacklist.put("/night", Rank.STAFF_COPPER);
		commandBlacklist.put("/storm", Rank.STAFF_COPPER);
		commandBlacklist.put("/rain", Rank.STAFF_COPPER);
		commandBlacklist.put("/sun", Rank.STAFF_COPPER);
		commandBlacklist.put("/time", Rank.STAFF_COPPER);
		//
		commandBlacklist.put("/sky", Rank.MODERATOR);
		commandBlacklist.put("/ip", Rank.MODERATOR);
		commandBlacklist.put("/alt", Rank.MODERATOR);
		//
		commandBlacklist.put("/chimney", Rank.ARCHITECT);
		//
		commandBlacklist.put("/butcher", Rank.ADVANCED_BUILDER);
		commandBlacklist.put("/killall", Rank.ADVANCED_BUILDER);
		commandBlacklist.put("/mobkill", Rank.ADVANCED_BUILDER);
		commandBlacklist.put("/remove", Rank.ADVANCED_BUILDER);
	}
}
