package com.amentrix.evilbook.main;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.statistics.GlobalStatistic;

import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;

/**
 * Schedulers and timed events
 * @author Reece Aaron Lecrivain
 */
class Scheduler {
	private EvilBook plugin;
	private Random rand = new Random();
	
	/**
	 * Define a new scheduler
	 * @param evilBook The parent EvilBook plugin
	 */
	Scheduler(EvilBook evilBook) {
		this.plugin = evilBook;
	}

	/**
	 * Autosave the player profiles and display a tip
	 */
	void tipsAutosave() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			@Override
			public void run() {
				int random = Scheduler.this.rand.nextInt(100);
				for (final Player player : plugin.getServer().getOnlinePlayers()) {
					final PlayerProfile profile = EvilBook.getProfile(player);
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
								ChatExtensions.sendClickableMessage(player, "§c❤ §dYou can §l/donate §dto support the server §c❤", EnumClickAction.SUGGEST_COMMAND, "/donate");
							} else if (random >= 50 && random < 65) {
								ChatExtensions.sendClickableMessage(player, "§c⚔ §dYou can play survival §l/survival §c⚔", EnumClickAction.SUGGEST_COMMAND, "/survival");
							} else if (random >= 65 && random < 80) {
								ChatExtensions.sendClickableMessage(player, "§c⚒ §dYou can play minigames §l/minigame §c⚒", EnumClickAction.SUGGEST_COMMAND, "/minigame");
							} else {
								ChatExtensions.sendClickableMessage(player, "§c✔ §dComplete §l/achievements §dfor unique rewards §c✔", EnumClickAction.SUGGEST_COMMAND, "/achievements");
							}
						} else {
							if (random >= 0 && random < 60) {
								ChatExtensions.sendClickableMessage(player, "§c❤ §dYou can §l/donate §dfor a higher rank §c❤", EnumClickAction.SUGGEST_COMMAND, "/donate");
							} else if (random >= 50 && random < 65) {
								ChatExtensions.sendClickableMessage(player, "§c⚔ §dYou can play survival §l/survival §c⚔", EnumClickAction.SUGGEST_COMMAND, "/survival");
							} else if (random >= 65 && random < 80) {
								ChatExtensions.sendClickableMessage(player, "§c⚒ §dYou can play minigames §l/minigame §c⚒", EnumClickAction.SUGGEST_COMMAND, "/minigame");
							} else {
								ChatExtensions.sendClickableMessage(player, "§c✔ §dComplete §l/achievements §dfor unique rewards §c✔", EnumClickAction.SUGGEST_COMMAND, "/achievements");
							}
						}
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 20;
								GlobalStatistic.incrementStatistic(GlobalStatistic.EconomyGrowth, 20);
								profile.saveProfile();
							}
						});
					} else {
						if (random >= 0 && random < 70) {
							ChatExtensions.sendClickableMessage(player, "§c❤ §dDonate to become an admin §l/donate §c❤", EnumClickAction.SUGGEST_COMMAND, "/donate");
						} else if (random >= 70 && random < 75) {
							ChatExtensions.sendClickableMessage(player, "§c⚔ §dYou can play survival §l/survival §c⚔", EnumClickAction.SUGGEST_COMMAND, "/survival");
						} else if (random >= 75 && random < 80) {
							ChatExtensions.sendClickableMessage(player, "§c⚒ §dYou can play minigames §l/minigame §c⚒", EnumClickAction.SUGGEST_COMMAND, "/minigame");
						} else if (random >= 80 && random < 90) {
							ChatExtensions.sendClickableMessage(player, "§c✔ §dComplete §l/achievements §dfor unique rewards §c✔", EnumClickAction.SUGGEST_COMMAND, "/achievements");
						} else {
							ChatExtensions.sendClickableMessage(player, "§c✉ §dYou can give feedback on the server §l/feedback §c✉", EnumClickAction.SUGGEST_COMMAND, "/feedback");
						}
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								EvilBook.playerProfiles.get(player.getName().toLowerCase()).money += 10;
								GlobalStatistic.incrementStatistic(GlobalStatistic.EconomyGrowth, 10);
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
	void updateServices() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			@Override
			public void run() {
				// Dynamic Signs
				for (final World world : EvilBook.dynamicSignList.keySet()) {
					if (world.getPlayers().size() != 0) {
						for (DynamicSign dynamicSign : EvilBook.dynamicSignList.get(world)) {
							dynamicSign.update();
						}
					}
				}
				// Emitters
				for (Emitter emit : EvilBook.emitterList) {
					emit.update();
				}
			}
		}, 0L, 10L);
	}

	/**
	 * Update disguises
	 */
	void updateDisguise() {
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