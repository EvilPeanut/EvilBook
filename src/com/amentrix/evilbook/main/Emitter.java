package com.amentrix.evilbook.main;

import java.sql.ResultSet;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Emitter instance
 * @author Reece Aaron Lecrivain
 */
public class Emitter {
	public Location location;
	public EmitterEffect effect;
	private int data, frequency, frequencyTick = 0;

	public Emitter(Location location, EmitterEffect effect, int data, int frequency) {
		this.location = location;
		this.effect = effect;
		this.data = data;
		this.frequency = frequency;
	}
	
	public Emitter(EvilBook plugin, ResultSet properties) {
		try {
			this.location = new Location(plugin.getServer().getWorld(properties.getString("world")), properties.getInt("x"), properties.getInt("y"), properties.getInt("z"));
			this.effect = EmitterEffect.valueOf(properties.getString("effect"));
			this.data = properties.getInt("data");
			this.frequency = properties.getInt("frequency");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void update() {
		if (!this.location.getBlock().getChunk().isLoaded()) return;
		if (this.frequencyTick == this.frequency) {
			if (this.effect.isParticleEffect()) {
				EvilBook.sendParticlesPacket(this.location, this.effect.particleName, 1, this.data);
			} else if (this.effect == EmitterEffect.Smoke) {
				this.location.getWorld().playEffect(this.location, Effect.SMOKE, this.data);
			} else if (this.effect == EmitterEffect.Flames) {
				this.location.getWorld().playEffect(this.location, Effect.MOBSPAWNER_FLAMES, this.data);
			} else if (this.effect == EmitterEffect.Potion) {
				this.location.getWorld().playEffect(this.location, Effect.POTION_BREAK, this.data);
			}
			this.frequencyTick = 0;
		} else {
			this.frequencyTick++;
		}
	}
	
	public void save() {
		try {
			SQL.insert(TableType.Emitter, 
					"'" + this.location.getWorld().getName() + "','" + this.location.getBlockX() +
					"','" + this.location.getBlockY() + "','" + this.location.getBlockZ() +
					"','" + this.effect + "','" + this.data + "','" + this.frequency + "'");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
