package com.amentrix.evilbook.main;

/**
 * Emitter effect enumerator
 * @author Reece Aaron Lecrivain
 */
public enum EmitterEffect {
	Smoke(null, Rank.Architect),
	Flames(null, Rank.Architect),
	Hearts("heart", Rank.Architect),
	RedstoneFumes("reddust", Rank.Architect),
	Slime("slime", Rank.Architect),
	MagicCriticalHit("magicCrit", Rank.Architect),
	EnchantmentTable("enchantmenttable", Rank.Architect),
	Cloud("cloud", Rank.Admin),
	Potion(null, Rank.Tycoon),
	HugeExplosion("hugeexplosion", Rank.Tycoon),
	LargeExplosion("largeexplode", Rank.Tycoon),
	FireworksSpark("fireworksSpark", Rank.Tycoon);
	
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
