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
	private EmitterEffect effect;
	private int data, frequency, frequencyTick = 0;

	Emitter(Location location, EmitterEffect effect, int data, int frequency) {
		this.location = location;
		this.effect = effect;
		this.data = data;
		this.frequency = frequency;
	}
	
	Emitter(EvilBook plugin, ResultSet properties) {
		try {
			this.location = new Location(plugin.getServer().getWorld(properties.getString("world")), properties.getInt("x"), properties.getInt("y"), properties.getInt("z"));
			this.effect = EmitterEffect.parse(properties.getString("effect"));
			this.data = properties.getInt("data");
			this.frequency = properties.getInt("frequency");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void update() {
		if (!this.location.getBlock().getChunk().isLoaded()) return;
		if (this.frequencyTick == this.frequency) {
			switch (this.effect) {
			case Cloud:
				this.location.getWorld().playEffect(this.location, Effect.CLOUD, this.data);
				break;
			case EnchantmentTable:
				this.location.getWorld().playEffect(this.location, Effect.FLYING_GLYPH, this.data);
				break;
			case FireworksSpark:
				this.location.getWorld().playEffect(this.location, Effect.FIREWORKS_SPARK, this.data);
				break;
			case Flames:
				this.location.getWorld().playEffect(this.location, Effect.MOBSPAWNER_FLAMES, this.data);
				break;
			case Hearts:
				this.location.getWorld().playEffect(this.location, Effect.HEART, this.data);
				break;
			case HugeExplosion:
				this.location.getWorld().playEffect(this.location, Effect.EXPLOSION_HUGE, this.data);
				break;
			case LargeExplosion:
				this.location.getWorld().playEffect(this.location, Effect.EXPLOSION_LARGE, this.data);
				break;
			case MagicCriticalHit:
				this.location.getWorld().playEffect(this.location, Effect.MAGIC_CRIT, this.data);
				break;
			case Potion:
				this.location.getWorld().playEffect(this.location, Effect.POTION_BREAK, this.data);
				break;
			case RedstoneFumes:
				this.location.getWorld().playEffect(this.location, Effect.COLOURED_DUST, this.data);
				break;
			case Slime:
				this.location.getWorld().playEffect(this.location, Effect.SLIME, this.data);
				break;
			case Smoke:
				this.location.getWorld().playEffect(this.location, Effect.SMOKE, this.data);
				break;
			case LavaDrip:
				this.location.getWorld().playEffect(this.location, Effect.LAVADRIP, this.data);
				break;
			case LavaPop:
				this.location.getWorld().playEffect(this.location, Effect.LAVA_POP, this.data);
				break;
			case Note:
				this.location.getWorld().playEffect(this.location, Effect.NOTE, this.data);
				break;
			case Portal:
				this.location.getWorld().playEffect(this.location, Effect.PORTAL, this.data);
				break;
			case Thundercloud:
				this.location.getWorld().playEffect(this.location, Effect.VILLAGER_THUNDERCLOUD, this.data);
				break;
			case VoidFog:
				this.location.getWorld().playEffect(this.location, Effect.VOID_FOG, this.data);
				break;
			case WaterDrip:
				this.location.getWorld().playEffect(this.location, Effect.WATERDRIP, this.data);
				break;
			default:
				break;
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
