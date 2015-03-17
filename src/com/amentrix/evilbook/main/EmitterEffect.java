package com.amentrix.evilbook.main;

/**
 * Emitter effect enumerator
 * @author Reece Aaron Lecrivain
 */
enum EmitterEffect {
	Smoke(Rank.ARCHITECT),
	LavaPop(Rank.ARCHITECT),
	LavaDrip(Rank.ARCHITECT),
	Note(Rank.ARCHITECT),
	Portal(Rank.ARCHITECT),
	Thundercloud(Rank.ARCHITECT),
	VoidFog(Rank.ARCHITECT),
	WaterDrip(Rank.ARCHITECT),
	Flames(Rank.ARCHITECT),
	Hearts(Rank.ARCHITECT),
	RedstoneFumes(Rank.ARCHITECT),
	Slime(Rank.ARCHITECT),
	MagicCriticalHit(Rank.ARCHITECT),
	EnchantmentTable(Rank.ARCHITECT),
	Cloud(Rank.ADMIN),
	Potion(Rank.TYCOON),
	HugeExplosion(Rank.TYCOON),
	LargeExplosion(Rank.TYCOON),
	FireworksSpark(Rank.TYCOON);
	
	public Rank minimumRank;
	
	EmitterEffect(Rank rank) {
		this.minimumRank = rank;
	}
	
	static boolean contains(String test) {
	    for (EmitterEffect e : EmitterEffect.values()) {
	        if (e.name().equalsIgnoreCase(test)) return true;
	    }
	    return false;
	}
	
	static EmitterEffect parse(String test) {
	    for (EmitterEffect e : EmitterEffect.values()) {
	        if (e.name().equalsIgnoreCase(test)) return e;
	    }
	    return null;
	}
}
