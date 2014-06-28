package com.amentrix.evilbook.main;

import java.util.Random;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.statistics.Statistic;
import com.amentrix.evilbook.statistics.Statistics;

/**
 * Schedulers and timed events
 * @author Reece Aaron Lecrivain
 */
public class Scheduler {
	EvilBook plugin;
	Random rand = new Random();
	
	/**
	 * Define a new scheduler
	 * @param evilBook The parent EvilBook plugin
	 */
	public Scheduler(EvilBook evilBook) {
		this.plugin = evilBook;
	}

	/**
	 * Autosave the player profiles and display a tip
	 */
	public void tipsAutosave() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			@Override
			public void run() {
				int random = rand.nextInt(100);
				for (Player p : Scheduler.this.plugin.getServer().getOnlinePlayers()) {
					if (EvilBook.getProfile(p).isAway) continue;
					if (System.currentTimeMillis() - EvilBook.getProfile(p).lastActionTime > 120000) {
						EvilBook.getProfile(p).isAway = true;
						EvilBook.broadcastPlayerMessage(p.getName(), "§5" + p.getDisplayName() + " §dhas gone AFK");
						EvilBook.getProfile(p).updatePlayerListName();
						continue;
					}
					if (EvilBook.getProfile(p).rank.isAdmin()) {
						if (EvilBook.getProfile(p).rank.isHigher(Rank.Investor)) {
							if (random >= 0 && random < 60) {
								p.sendMessage("§dYou can always §l/donate §dto support the server");
							} else if (random >= 60 && random < 80) {
								p.sendMessage("§dYou can play survival §l/survival §don Amentrix");
							} else if (random >= 80 && random < 100) {
								p.sendMessage("§dYou can complete achievements §l/achievement §dfor rewards");
							}
						} else {
							if (random >= 0 && random < 60) {
								p.sendMessage("§dYou can always §l/donate §dagain for a higher rank");
							} else if (random >= 60 && random < 80) {
								p.sendMessage("§dYou can play survival §l/survival §don Amentrix");
							} else if (random >= 80 && random < 100) {
								p.sendMessage("§dYou can complete achievements §l/achievement §dfor rewards");
							}
						}
						EvilBook.playerProfiles.get(p.getName().toLowerCase()).money += 20;
						Statistics.incrementStatistic(Statistic.EconomyGrowth, 20);
						EvilBook.playerProfiles.get(p.getName().toLowerCase()).setProperty("Money", Integer.toString(EvilBook.playerProfiles.get(p.getName().toLowerCase()).money));
					} else {
						if (random >= 0 && random < 60) {
							p.sendMessage("§dDonate to become an admin §l/donate");
						} else if (random >= 60 && random < 80) {
							p.sendMessage("§dYou can play survival §l/survival §don Amentrix");
						} else if (random >= 80 && random < 100) {
							p.sendMessage("§dYou can complete achievements §l/achievement §dfor rewards");
						}
						EvilBook.playerProfiles.get(p.getName().toLowerCase()).money += 10;
						Statistics.incrementStatistic(Statistic.EconomyGrowth, 10);
						EvilBook.playerProfiles.get(p.getName().toLowerCase()).setProperty("Money", Integer.toString(EvilBook.playerProfiles.get(p.getName().toLowerCase()).money));
					}
					EvilBook.getProfile(p).saveProfile();
				}
			}
		}, 0L, 6000L);
	}
	
	/**
	 * Update the services
	 */
	public void updateServices() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			@Override
			public void run() {
				// Dynamic Signs
				for (DynamicSign dynamicSign : EvilBook.dynamicSignList) {
					if (dynamicSign.location.getBlock().getState() instanceof Sign == false) continue;
					Sign sign = (Sign) dynamicSign.location.getBlock().getState();
					if (!sign.getChunk().isLoaded()) continue;
					String time = EvilBook.getTime(dynamicSign.location.getWorld());
					String weather = EvilBook.getWeather(sign.getBlock());
					sign.setLine(0, dynamicSign.textLines[0].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(plugin.getServer().getOnlinePlayers().length)));
					sign.setLine(1, dynamicSign.textLines[1].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(plugin.getServer().getOnlinePlayers().length)));
					sign.setLine(2, dynamicSign.textLines[2].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(plugin.getServer().getOnlinePlayers().length)));
					sign.setLine(3, dynamicSign.textLines[3].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(plugin.getServer().getOnlinePlayers().length)));
					sign.update();
				}
				// Emitters
				for (Emitter emit : EvilBook.emitterList) {
					emit.update();
				}
				// Mob Disguise
				for (Player p : Scheduler.this.plugin.getServer().getOnlinePlayers()) {
					if (EvilBook.getProfile(p).disguise != null) {
						EvilBook.getProfile(p).disguise.teleport(p.getLocation());
					}
				}
			}
		}, 0L, 1L);
	}

}