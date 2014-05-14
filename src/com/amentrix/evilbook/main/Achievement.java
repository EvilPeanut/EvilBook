package com.amentrix.evilbook.main;

/**
 * Achievement enumerator
 * @author Reece Aaron Lecrivain
 */
public enum Achievement {
	SECOND_JOIN("Great Return"),
	SURVIVAL_MINED_DIAMOND("Diamond Standard");
	
	private String name;
	
	Achievement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static Achievement getByName(String name) {
		switch (name) {
		case "SECOND_JOIN": return Achievement.SECOND_JOIN;
		case "SURVIVAL_MINED_DIAMOND": return Achievement.SURVIVAL_MINED_DIAMOND;
		default: return null;
		}
	}
}
