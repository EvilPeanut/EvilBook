package com.amentrix.evilbook.main;

/**
 * Emitter effect enumerator
 * @author Reece Aaron Lecrivain
 */
public enum EmitterEffect {
	Smoke(null, Rank.ARCHITECT),
	Flames(null, Rank.ARCHITECT),
	Hearts("heart", Rank.ARCHITECT),
	RedstoneFumes("reddust", Rank.ARCHITECT),
	Slime("slime", Rank.ARCHITECT),
	MagicCriticalHit("magicCrit", Rank.ARCHITECT),
	EnchantmentTable("enchantmenttable", Rank.ARCHITECT),
	Cloud("cloud", Rank.ADMIN),
	Potion(null, Rank.TYCOON),
	HugeExplosion("hugeexplosion", Rank.TYCOON),
	LargeExplosion("largeexplode", Rank.TYCOON),
	FireworksSpark("fireworksSpark", Rank.TYCOON);
	
	public String particleName;
	public Rank minimumRank;
	
	EmitterEffect(String name, Rank rank) {
		this.particleName = name;
		this.minimumRank = rank;
	}
	
	public static boolean contains(String test) {
	    for (EmitterEffect e : EmitterEffect.values()) {
	        if (e.name().equalsIgnoreCase(test)) return true;
	    }
	    return false;
	}
	
	public static EmitterEffect parse(String test) {
	    for (EmitterEffect e : EmitterEffect.values()) {
	        if (e.name().equalsIgnoreCase(test)) return e;
	    }
	    return null;
	}
	
	public Boolean isParticleEffect() {
		return this.particleName == null ? false : true;
	}
}
