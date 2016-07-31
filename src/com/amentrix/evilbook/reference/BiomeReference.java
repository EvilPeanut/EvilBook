package com.amentrix.evilbook.reference;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Biome;

/**
 * Biome reference utility class
 * @author Reece Aaron Lecrivain
 */
public class BiomeReference {
	private static final Map<Biome, List<String>> biomeList = new LinkedHashMap<>();
	
	static {
		biomeList.put(Biome.OCEAN, Arrays.asList("Ocean", "Sea"));
		biomeList.put(Biome.PLAINS, Arrays.asList("Plains", "Plain"));
		biomeList.put(Biome.DESERT, Arrays.asList("Desert"));
		biomeList.put(Biome.FOREST, Arrays.asList("Forest"));
		biomeList.put(Biome.TAIGA, Arrays.asList("Taiga"));
		biomeList.put(Biome.SWAMPLAND, Arrays.asList("Swampland", "Swamp"));
		biomeList.put(Biome.RIVER, Arrays.asList("River"));
		biomeList.put(Biome.HELL, Arrays.asList("Hell"));
		biomeList.put(Biome.SKY, Arrays.asList("Sky"));
		biomeList.put(Biome.FROZEN_OCEAN, Arrays.asList("Frozen Ocean", "FrozenOcean", "FrozenSea"));
		biomeList.put(Biome.FROZEN_RIVER, Arrays.asList("Frozen River", "FrozenRiver"));
		biomeList.put(Biome.ICE_FLATS, Arrays.asList("Ice Plains", "IcePlains", "IcePlain"));
		biomeList.put(Biome.ICE_MOUNTAINS, Arrays.asList("Ice Mountains", "IceMountains", "IceMountain"));
		biomeList.put(Biome.MUSHROOM_ISLAND, Arrays.asList("Mushroom Island", "MushroomIsland", "Mushroom"));
		biomeList.put(Biome.BEACHES, Arrays.asList("Beach"));
		biomeList.put(Biome.JUNGLE, Arrays.asList("Jungle"));
		biomeList.put(Biome.SAVANNA, Arrays.asList("Savanna"));
		biomeList.put(Biome.MESA, Arrays.asList("MESA"));
	}
	
	/**
	 * Return the biome type from its string name
	 * @param biome The biome name
	 * @return The biome type
	 */
	public static Biome getBiome(String biome) {
		for (Entry<Biome, List<String>> entry : biomeList.entrySet()) {
			if (entry.getValue() == null) continue;
			for (String subItem : entry.getValue()) {
				if (subItem != null && biome.equalsIgnoreCase(subItem)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
