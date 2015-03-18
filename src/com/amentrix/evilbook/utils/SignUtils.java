package com.amentrix.evilbook.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;

import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;

public class SignUtils {
	
	public static void formatDynamicSign(Sign sign) {
		String[] line = sign.getLines();
		for (int i = 0; i < 4; i++) {
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Time]", "[time]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Weather]", "[weather]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Online]", "[online]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[BlocksBroken]", "[blocksbroken]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[BlocksPlaced]", "[blocksplaced]");
		}
		DynamicSign dynamicSign = new DynamicSign(sign.getBlock().getLocation(), line);
		EvilBook.dynamicSignList.get(sign.getWorld()).add(dynamicSign);
		updateDynamicSign(dynamicSign);
	}

	public static void formatSignText(Sign sign) {
		for (int i = 0; i < 4; i++) sign.setLine(i, EvilBook.toFormattedString(sign.getLine(i)));
	}
	
	public static void updateDynamicSign(DynamicSign dynamicSign) {
		if (!dynamicSign.location.getChunk().isLoaded()) return;
		if (dynamicSign.location.getBlock().getState() instanceof Sign == false) return;
		//EvilBook.logInfo("Updating dynamic sign at " + dynamicSign.location.getWorld().getName() +
				//", " + dynamicSign.location.getBlockX() + ", " + dynamicSign.location.getBlockY() + ", " + dynamicSign.location.getBlockZ());
		Sign sign = (Sign) dynamicSign.location.getBlock().getState();
		for (int i = 0; i < 4; i++) {
			sign.setLine(i, dynamicSign.textLines[i].replace("[time]", EvilBook.getTime(sign.getBlock().getWorld())
					.replace("[weather]", EvilBook.getWeather(sign.getBlock())))
					.replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
					.replace("[blocksbroken]", GlobalStatistics.getStatistic(GlobalStatistic.BlocksBroken))
					.replace("[blocksplaced]", GlobalStatistics.getStatistic(GlobalStatistic.BlocksPlaced))
					);
		}
		sign.update();
	}
	
	public static Boolean isDynamicSign(Sign sign) {
		return sign.getLine(0).toLowerCase().contains("[time]") || 
			   sign.getLine(1).toLowerCase().contains("[time]") || 
			   sign.getLine(2).toLowerCase().contains("[time]") || 
			   sign.getLine(3).toLowerCase().contains("[time]") ||
			   sign.getLine(0).toLowerCase().contains("[weather]") || 
			   sign.getLine(1).toLowerCase().contains("[weather]") || 
			   sign.getLine(2).toLowerCase().contains("[weather]") || 
			   sign.getLine(3).toLowerCase().contains("[weather]") ||
			   sign.getLine(0).toLowerCase().contains("[online]") || 
			   sign.getLine(1).toLowerCase().contains("[online]") || 
			   sign.getLine(2).toLowerCase().contains("[online]") || 
			   sign.getLine(3).toLowerCase().contains("[online]") ||
			   sign.getLine(0).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(1).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(2).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(3).toLowerCase().contains("[blocksbroken]") ||
			   sign.getLine(0).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(1).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(2).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(3).toLowerCase().contains("[blocksplaced]");
	}
	
}
