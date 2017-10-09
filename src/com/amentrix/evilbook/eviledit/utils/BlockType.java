package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.eviledit.Session;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.utils.BlockReference;

public class BlockType {
	private Material material;
	private Byte data = 0;
	
	public BlockType(String blockName) {
		this.material = BlockReference.getBlockMaterial(blockName);
	}
	
	public Boolean isValid(Player player) {
		if (this.material == null) {
			player.sendMessage("§7Please enter a valid block name or ID");
			return false;
		} else if (Session.isBlocked(this.material)) {
			player.sendMessage("§cThis block is banned in EvilEdit");
			return false;
		}
		return true;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public Byte getData() {
		return this.data;
	}
	
	public void setData(String blockData) {
		if (EvilBook.isByte(blockData)) {
			this.data = Byte.parseByte(blockData);
		} else {
			if (this.material == Material.STONE) {
				switch (blockData.toLowerCase()) {
				case "granite":
					this.data = 1;
					break;
				case "polishedgranite":
					this.data = 2;
					break;
				case "diorite":
					this.data = 3;
					break;
				case "polisheddiorite":
					this.data = 4;
					break;
				case "andesite":
					this.data = 5;
					break;
				case "polishedandesite":
					this.data = 6;
					break;
				default:
					break;
				}
			} else if (this.material == Material.WOOD || this.material == Material.WOOD_DOUBLE_STEP ||
					this.material == Material.WOOD_STEP || this.material == Material.WOOD_STAIRS || this.material == Material.SAPLING) {
				switch (blockData.toLowerCase()) {
				case "spruce":
					this.data = 1;
					break;
				case "birch":
					this.data = 2;
					break;
				case "jungle":
					this.data = 3;
					break;
				case "acacia":
					this.data = 4;
					break;
				case "darkoak":
					this.data = 5;
					break;
				default:
					break;
				}
			} else if (this.material == Material.SAND) {
				switch (blockData.toLowerCase()) {
				case "red":
					this.data = 1;
					break;
				default:
					break;
				}
			} else if (this.material == Material.LOG) {
				switch (blockData.toLowerCase()) {
				case "spruce":
					this.data = 1;
					break;
				case "birch":
					this.data = 2;
					break;
				case "jungle":
					this.data = 3;
					break;
				case "acacia":
					this.material = Material.LOG_2;
					break;
				case "darkoak":
					this.material = Material.LOG_2;
					this.data = 1;
					break;
				default:
					break;
				}
			} else if (this.material == Material.LEAVES) {
				switch (blockData.toLowerCase()) {
				case "spruce":
					this.data = 1;
					break;
				case "birch":
					this.data = 2;
					break;
				case "jungle":
					this.data = 3;
					break;
				case "acacia":
					this.material = Material.LEAVES_2;
					break;
				case "darkoak":
					this.material = Material.LEAVES_2;
					this.data = 1;
					break;
				default:
					break;
				}
			} else if (this.material == Material.SPONGE) {
				switch (blockData.toLowerCase()) {
				case "wet":
					this.data = 1;
					break;
				default:
					break;
				}
			} else if (this.material == Material.SANDSTONE || this.material == Material.RED_SANDSTONE) {
				switch (blockData.toLowerCase()) {
				case "chiseled":
					this.data = 1;
					break;
				case "smooth":
					this.data = 2;
					break;
				default:
					break;
				}
			} else if (this.material == Material.LONG_GRASS) {
				switch (blockData.toLowerCase()) {
				case "grass":
					this.data = 1;
					break;
				case "fern":
					this.data = 2;
					break;
				default:
					break;
				}
			} else if (this.material == Material.WOOL || this.material == Material.CARPET || this.material == Material.STAINED_GLASS 
					|| this.material == Material.STAINED_GLASS_PANE || this.material == Material.STAINED_CLAY
					|| this.material == Material.CONCRETE || this.material == Material.CONCRETE_POWDER) {
				switch (blockData.toLowerCase()) {
				case "orange":
					this.data = 1;
					break;
				case "magenta":
					this.data = 2;
					break;
				case "lightblue":
					this.data = 3;
					break;
				case "yellow":
					this.data = 4;
					break;
				case "lime":
					this.data = 5;
					break;
				case "pink":
					this.data = 6;
					break;
				case "gray":
					this.data = 7;
					break;
				case "grey":
					this.data = 7;
					break;
				case "lightgray":
					this.data = 8;
					break;
				case "lightgrey":
					this.data = 8;
					break;
				case "cyan":
					this.data = 9;
					break;
				case "purple":
					this.data = 10;
					break;
				case "blue":
					this.data = 11;
					break;
				case "brown":
					this.data = 12;
					break;
				case "green":
					this.data = 13;
					break;
				case "red":
					this.data = 14;
					break;
				case "black":
					this.data = 15;
					break;
				default:
					break;
				}
			} else if (this.material == Material.STEP || this.material == Material.DOUBLE_STEP) {
				switch (blockData.toLowerCase()) {
				case "sandstone":
					this.data = 1;
					break;
				case "wood":
					this.data = 2;
					break;
				case "wooden":
					this.data = 2;
					break;
				case "cobblestone":
					this.data = 3;
					break;
				case "cobble":
					this.data = 3;
					break;
				case "brick":
					this.data = 4;
					break;
				case "stonebrick":
					this.data = 5;
					break;
				case "netherbrick":
					this.data = 6;
					break;
				case "quartz":
					this.data = 7;
					break;
				default:
					break;
				}
			} else if (this.material == Material.DOUBLE_PLANT) {
				switch (blockData.toLowerCase()) {
				case "lilac":
					this.data = 1;
					break;
				case "tallgrass":
					this.data = 2;
					break;
				case "fern":
					this.data = 3;
					break;
				case "rose":
					this.data = 4;
					break;
				case "peony":
					this.data = 5;
					break;
				default:
					break;
				}
			} else if (this.material == Material.PRISMARINE) {
				switch (blockData.toLowerCase()) {
				case "brick":
					this.data = 1;
					break;
				case "bricks":
					this.data = 1;
					break;
				case "dark":
					this.data = 2;
					break;
				default:
					break;
				}
			} else if (this.material == Material.QUARTZ_BLOCK) {
				switch (blockData.toLowerCase()) {
				case "chiseled":
					this.data = 1;
					break;
				case "pillar":
					this.data = 2;
					break;
				default:
					break;
				}
			} else if (this.material == Material.COBBLE_WALL) {
				switch (blockData.toLowerCase()) {
				case "moss":
					this.data = 1;
					break;
				case "mossy":
					this.data = 1;
					break;
				default:
					break;
				}
			} else if (this.material == Material.SMOOTH_BRICK) {
				switch (blockData.toLowerCase()) {
				case "moss":
					this.data = 1;
					break;
				case "mossy":
					this.data = 1;
					break;
				case "cracked":
					this.data = 2;
					break;
				case "chiseled":
					this.data = 3;
					break;
				default:
					break;
				}
			} else if (this.material == Material.RED_ROSE) {
				switch (blockData.toLowerCase()) {
				case "blue":
					this.data = 1;
					break;
				case "orchid":
					this.data = 1;
					break;
				case "allium":
					this.data = 2;
					break;
				case "azure":
					this.data = 3;
					break;
				case "bluet":
					this.data = 3;
					break;
				case "red":
					this.data = 4;
					break;
				case "orange":
					this.data = 5;
					break;
				case "white":
					this.data = 6;
					break;
				case "pink":
					this.data = 7;
					break;
				case "oxeye":
					this.data = 8;
					break;
				case "daisy":
					this.data = 8;
					break;
				default:
					break;
				}
			} else if (this.material == Material.LONG_GRASS) {
				switch (blockData.toLowerCase()) {
				case "grass":
					this.data = 1;
					break;
				case "fern":
					this.data = 2;
					break;
				default:
					break;
				}
			} else if (this.material == Material.DIRT) {
				switch (blockData.toLowerCase()) {
				case "coarse":
					this.data = 1;
					break;
				case "podzol":
					this.data = 2;
					break;
				default:
					break;
				}
			}
		}
	}
}
