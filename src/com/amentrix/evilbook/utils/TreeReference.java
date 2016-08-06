package com.amentrix.evilbook.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.TreeType;

/**
 * Tree type reference utility class
 * @author Reece Aaron Lecrivain
 */
public class TreeReference {
	public static final Map<TreeType, List<String>> treeTypeList = new LinkedHashMap<>();
	
	static {
		treeTypeList.put(TreeType.ACACIA, Arrays.asList("Acacia"));
		treeTypeList.put(TreeType.BIG_TREE, Arrays.asList("Big Tree", "BigTree"));
		treeTypeList.put(TreeType.BIRCH, Arrays.asList("Birch"));
		treeTypeList.put(TreeType.BROWN_MUSHROOM, Arrays.asList("Brown Mushroom", "BrownMushroom"));
		treeTypeList.put(TreeType.DARK_OAK, Arrays.asList("Dark Oak", "DarkOak"));
		treeTypeList.put(TreeType.JUNGLE, Arrays.asList("Jungle"));
		treeTypeList.put(TreeType.JUNGLE_BUSH, Arrays.asList("Jungle Bush", "JungleBush"));
		treeTypeList.put(TreeType.MEGA_REDWOOD, Arrays.asList("Mega Redwood", "MegaRedwood"));
		treeTypeList.put(TreeType.RED_MUSHROOM, Arrays.asList("Red Mushroom", "RedMushroom"));
		treeTypeList.put(TreeType.REDWOOD, Arrays.asList("Redwood"));
		treeTypeList.put(TreeType.SMALL_JUNGLE, Arrays.asList("Small Jungle", "SmallJungle"));
		treeTypeList.put(TreeType.SWAMP, Arrays.asList("Swamp"));
		treeTypeList.put(TreeType.TALL_BIRCH, Arrays.asList("Tall Birch", "TallBirch"));
		treeTypeList.put(TreeType.TALL_REDWOOD, Arrays.asList("Tall Redwood", "TallRedwood"));
		treeTypeList.put(TreeType.TREE, Arrays.asList("Tree"));
	}
	
	/**
	 * Return the tree type from its string name
	 * @param treeType The tree type name
	 * @return The tree type
	 */
	public static TreeType getTreeType(String treeType) {
		for (Entry<TreeType, List<String>> entry : treeTypeList.entrySet()) {
			if (entry.getValue() == null) continue;
			for (String subItem : entry.getValue()) {
				if (subItem != null && treeType.equalsIgnoreCase(subItem)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
