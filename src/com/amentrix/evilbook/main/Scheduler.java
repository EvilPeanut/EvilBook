package com.amentrix.evilbook.main;

import java.util.Random;

import org.bukkit.entity.Player;

import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;
import com.amentrix.evilbook.utils.SignUtils;

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
				int random = Scheduler.this.rand.nextInt(100);
				for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
					Player player = profile.getPlayer();
					if (profile.isAway) continue;
					if (profile.lastActionTime != 0 && System.currentTimeMillis() - profile.lastActionTime > 120000) {
						profile.isAway = true;
						EvilBook.broadcastPlayerMessage(player.getName(), "�5" + player.getDisplayName() + " �dhas gone AFK");
						profile.updatePlayerListName();
						continue;
					}
					if (profile.rank.isAdmin()) {
						if (profile.rank.isHigher(Rank.INVESTOR)) {
							if (random >= 0 && random < 60) {
								player.sendMessage("�dYou can always �l/donate �dto support the server");
							} else if (random >= 60 && random < 80) {
								player.sendMessage("�dYou can play survival �l/survival �don Amentrix");
							} else {
								player.sendMessage("�dYou can complete achievements �l/achievement �dfor rewards");
							}
						} else {
							if (random >= 0 && random < 60) {
								player.sendMessage("�dYou can always �l/donate �dagain for a higher rank");
							} else if (random >= 60 && random < 80) {
								player.sendMessage("�dYou can play survival �l/survival �don Amentrix");
							} else {
								player.sendMessage("�dYou can complete achievements �l/achievement �dfor rewards");
							}
						}
						EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 20;
						GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyGrowth, 20);
					} else {
						if (random >= 0 && random < 60) {
							player.sendMessage("�dDonate to become an admin �l/donate");
						} else if (random >= 60 && random < 70) {
							player.sendMessage("�dYou can play survival �l/survival �don Amentrix");
						} else if (random >= 70 && random < 80) {
							player.sendMessage("�dYou can complete achievements �l/achievement �dfor rewards");
						} else {
							player.sendMessage("�dEnter our competition to win Tycoon rank for free!");
							player.sendMessage("�bhttp://www.amentrix.com/Minecraft/Competition.htm");
						}
						EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 10;
						GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyGrowth, 10);
					}
					profile.saveProfile();
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
					SignUtils.updateDynamicSign(dynamicSign);
				}
				// Emitters
				for (Emitter emit : EvilBook.emitterList) {
					emit.update();
				}
				// Mob Disguise
				for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
					if (profile.disguise != null) {
						profile.disguise.teleport(profile.getPlayer().getLocation());
					}
				}
				/*
				for (Player p : Scheduler.this.plugin.getServer().getOnlinePlayers()) {
					if (EvilBook.getProfile(p).disguise != null) {
						EvilBook.getProfile(p).disguise.teleport(player.getLocation());
					}
				}
				*/
			}
		}, 0L, 1L);
	}

}