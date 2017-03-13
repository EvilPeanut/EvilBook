package com.amentrix.evilbook.main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;

public class DynamicSignManager {
	public static final Map<World, List<DynamicSign>> signList = new HashMap<>();
	public static String[] commands = {"[time]", "[weather]", "[online]", "[blocksbroken]", "[blocksplaced]"};
	
	public static void load() {
		for (World world : Bukkit.getServer().getWorlds()) {
			signList.put(world, new ArrayList());
		}
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.DynamicSign.getName() + ";")) {
				while (rs.next()) {
					World world = Bukkit.getServer().getWorld(UUID.fromString(rs.getString("world")));
					if (world != null) {
						signList.get(world).add(new DynamicSign(rs));
					} else {
						EvilBook.logInfo("Dynamic sign in " + rs.getString("world") + " at " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + " not loaded location unavailable");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static void update(DynamicSign dynamicSign) {
		if (dynamicSign.isSeen()) {
			Sign sign = (Sign) dynamicSign.location.getBlock().getState();
			for (int index = 0; index < 4; index++) {
				String line = dynamicSign.textLines[index];
				line = line.replace("[time]", EvilBook.getTime(sign.getBlock().getWorld()));
				line = line.replace("[weather]", EvilBook.getWeather(sign.getBlock()));
				line = line.replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
				line = line.replace("[blocksbroken]", GlobalStatistic.getStatistic(GlobalStatistic.BLOCKS_BROKEN));
				line = line.replace("[blocksplaced]", GlobalStatistic.getStatistic(GlobalStatistic.BLOCKS_PLACED));
				sign.setLine(index, line);
			}
			sign.update();
		}
	}
	
	public static void updateAll() {
		for (final World world : signList.keySet()) {
			if (world.getPlayers().size() != 0) {
				for (DynamicSign dynamicSign : signList.get(world)) {
					update(dynamicSign);
				}
			}
		}
	}
	
	public static Boolean isDynamicSign(Sign sign) {
		for (int index = 0; index < 4; index++) {
			for (String command : commands) {
				if (sign.getLine(index).toLowerCase().contains(command)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void formatDynamicSign(Sign sign) {
		String[] line = sign.getLines();
		for (int index = 0; index < 4; index++) {
			for (String command : commands) {
				line[index] = EvilBook.replaceAllIgnoreCase(line[index], command, command);
			}
		}
		DynamicSign dynamicSign = new DynamicSign(sign.getBlock().getLocation(), line);
		DynamicSignManager.signList.get(sign.getWorld()).add(dynamicSign);
		DynamicSignManager.update(dynamicSign);
	}
}
