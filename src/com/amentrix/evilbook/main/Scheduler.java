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
				for (final PlayerProfile profile : EvilBook.playerProfiles.values()) {
					final Player player = profile.getPlayer();
					if (profile.isAway) continue;
					if (profile.lastActionTime != 0 && System.currentTimeMillis() - profile.lastActionTime > 120000) {
						profile.isAway = true;
						EvilBook.broadcastPlayerMessage(player.getName(), "§5" + player.getDisplayName() + " §dhas gone AFK");
						profile.updatePlayerListName();
						continue;
					}
					if (profile.rank.isAdmin()) {
						if (profile.rank.isHigher(Rank.INVESTOR)) {
							if (random >= 0 && random < 50) {
								player.sendMessage("§c❤ §dYou can §l/donate §dto support the server §c❤");
							} else if (random >= 50 && random < 65) {
								player.sendMessage("§c⚔ §dYou can play survival §l/survival §c⚔");
							} else if (random >= 65 && random < 80) {
								player.sendMessage("§c⚒ §dYou can play minigames §l/minigame §c⚒");
							} else {
								player.sendMessage("§c✔ §dComplete §l/achievements §dfor unique rewards §c✔");
							}
						} else {
							if (random >= 0 && random < 60) {
								player.sendMessage("§c❤ §dYou can §l/donate §dfor a higher rank §c❤");
							} else if (random >= 50 && random < 65) {
								player.sendMessage("§c⚔ §dYou can play survival §l/survival §c⚔");
							} else if (random >= 65 && random < 80) {
								player.sendMessage("§c⚒ §dYou can play minigames §l/minigame §c⚒");
							} else {
								player.sendMessage("§c✔ §dComplete §l/achievements §dfor unique rewards §c✔");
							}
						}
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 20;
								GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyGrowth, 20);
								profile.saveProfile();
							}
						});
					} else {
						if (random >= 0 && random < 70) {
							player.sendMessage("§c❤ §dDonate to become an admin §l/donate §c❤");
						} else if (random >= 70 && random < 75) {
							player.sendMessage("§c⚔ §dYou can play survival §l/survival §c⚔");
						} else if (random >= 75 && random < 80) {
							player.sendMessage("§c⚒ §dYou can play minigames §l/minigame §c⚒");
						} else if (random >= 80 && random < 90) {
							player.sendMessage("§c✔ §dComplete §l/achievements §dfor unique rewards §c✔");
						} else {
							player.sendMessage("§c✉ §dYou can give feedback on the server §l/feedback §c✉");
						}
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 10;
								GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyGrowth, 10);
								profile.saveProfile();
							}
						});
					}
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
				//for (Emitter emit : EvilBook.emitterList) {
					//emit.update();
				//}
			}
		}, 0L, 20L);
	}

	/**
	 * Update disguises
	 */
	public void updateDisguise() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			@Override
			public void run() {
				// Mob Disguise
				for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
					if (profile.disguise != null) {
						profile.disguise.teleport(profile.getPlayer().getLocation());
					}
				}
			}
		}, 0L, 1L);
	}
}