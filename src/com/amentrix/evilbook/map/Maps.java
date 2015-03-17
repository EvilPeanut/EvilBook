package com.amentrix.evilbook.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.amentrix.evilbook.main.EvilBook;

/**
 *
 * @author cnaude
 */
public class Maps implements CommandExecutor {
	EvilBook plugin;
	
	private static final int MAGIC_NUMBER = Integer.MAX_VALUE - 395742;
	private File pluginFolder;
	private File cacheFolder;
	private File mapsFile;
	private static final HashMap<Short, String> mapIdList = new HashMap<>();
	private static final HashMap<Short, String> mapTypeList = new HashMap<>();

	public Maps(EvilBook plugin) {
		this.plugin = plugin;
		this.pluginFolder = new File("plugins/EvilBook/Maps");
		this.cacheFolder = new File(this.pluginFolder.getAbsolutePath() + "/Cache");
		this.mapsFile = new File(this.pluginFolder.getAbsolutePath() + "/Maps.txt");
		createDirStucture();
		plugin.getServer().getPluginManager().registerEvents(new MapListener(this), plugin);
		loadMapIdList();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				postWorldLoad();
			}
		}, 0);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				saveMapIdList();
			}
		}, 2400, 2400);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player == false) {
			sender.sendMessage("This command is not supported by the console");
			return true;
		}
		Player player = (Player) sender;
		if (command.getName().equalsIgnoreCase("map")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("head")) {
					String name = args[1];
					ArrayList<String> list = new ArrayList<>();
					list.add(name);
					for (String s : list) {
						ItemStack result = getMap(player, s, "face");
						if (!result.getType().equals(Material.EMPTY_MAP)) {
							if (player.getInventory().firstEmpty() > -1) {
								player.getInventory().addItem(result);
							} else {
								Location loc = player.getLocation().clone();
								World world = loc.getWorld();
								world.dropItemNaturally(loc, result);
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("body")) {
					String name = args[1];
					ArrayList<String> list = new ArrayList<>();
					list.add(name);
					for (String s : list) {
						ItemStack result = getMap(player, s, "body");
						if (!result.getType().equals(Material.EMPTY_MAP)) {
							if (player.getInventory().firstEmpty() > -1) {
								player.getInventory().addItem(result);
							} else {
								Location loc = player.getLocation().clone();
								World world = loc.getWorld();
								world.dropItemNaturally(loc, result);
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("url")) {
					String name = args[1];
					ArrayList<String> list = new ArrayList<>();
					list.add(name);
					for (String s : list) {
						ItemStack result = getMap(player, s, "url");
						if (!result.getType().equals(Material.EMPTY_MAP)) {
							if (player.getInventory().firstEmpty() > -1) {
								player.getInventory().addItem(result);
							} else {
								Location loc = player.getLocation().clone();
								World world = loc.getWorld();
								world.dropItemNaturally(loc, result);
							}
						}
					}
				} else {
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/map head [player]");
					sender.sendMessage("§d/map body [player]");
					sender.sendMessage("§d/map url [url]");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/map head [player]");
				sender.sendMessage("§d/map body [player]");
				sender.sendMessage("§d/map url [url]");
			}
			return true;
		}
		return false;
	}

	boolean downloadSkin(String pName) {
		try {
			URL website = new URL("http://skins.minecraft.net/MinecraftSkins/" + pName + ".png");
			try (ReadableByteChannel rbc = Channels.newChannel(website.openStream())) {
				try (FileOutputStream fos = new FileOutputStream(getFileName(pName))) {
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				}
			}
			return true;
		} catch (IOException exception) {
			return false;
		}
	}
	
	private boolean downloadURL(String url) {
		try {
			URL website = new URL(url);
			try (ReadableByteChannel rbc = Channels.newChannel(website.openStream())) {
				try (FileOutputStream fos = new FileOutputStream(getFileName(url.split("/")[url.split("/").length - 1].split(".png")[0]))) {
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				}
			}
			return true;
		} catch (IOException exception) {
			return false;
		}
	}

	private String getFileName(String name) {
		return this.cacheFolder.getAbsolutePath() + "/" + name + ".png";
	}

	private void postWorldLoad() {
		ArrayList<Short> badIds = new ArrayList<>();
		for (short mapId : mapIdList.keySet()) {
			String name = mapIdList.get(mapId);
			String type = mapTypeList.get(mapId);
			String fileName = getFileName(name);
			if (!new File(fileName).exists()) {
				if (type.equals("face") || type.equals("body")) {
					if (!downloadSkin(name)) {
						continue;
					}
				}
			}
			if (new File(fileName).exists()) {
				MapView mv = this.plugin.getServer().getMap(mapId);
				Render pr = new Render(fileName, type);
				if (mv != null) {
					for (MapRenderer mr : mv.getRenderers()) {
						mv.removeRenderer(mr);
					}
					mv.addRenderer(pr);
				} else {
					badIds.add(mapId);
				}
			}
		}
		for (short i : badIds) {
			if (mapIdList.containsKey(i)) {
				mapIdList.remove(i);
			}
			if (mapTypeList.containsKey(i)) {
				mapTypeList.remove(i);
			}
		}
		badIds.clear();
	}

	static void cleanLists(short mapId) {
		if (mapIdList.containsKey(mapId)) {
			mapIdList.remove(mapId);
		}
		if (mapTypeList.containsKey(mapId)) {
			mapTypeList.remove(mapId);
		}
	}

	void cleanup(String pName) {
		for (short mapId : mapIdList.keySet()) {
			MapView mv = this.plugin.getServer().getMap(mapId);
			if (mv != null) {
				for (MapRenderer mr : mv.getRenderers()) {
					if (mr instanceof Render) {
						((Render) mr).removePlayer(pName);
					}
				}
			}
		}
	}

	private void loadMapIdList() {
		if (this.mapsFile.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(this.mapsFile))) {
				String text;
				while ((text = reader.readLine()) != null) {
					String[] items = text.split(":", 3);
					mapIdList.put(Short.parseShort(items[0]), items[1]);
					if (items.length == 3) {
						mapTypeList.put(Short.parseShort(items[0]), items[2]);
					} else {
						mapTypeList.put(Short.parseShort(items[0]), "face");
					}
				}
			} catch (IOException exception) {
				//File isnt found? I think....?
			}
		}
	}

	public void saveMapIdList() {
		try (PrintWriter out = new PrintWriter(this.mapsFile)) {
			for (short mapId : mapIdList.keySet()) {
				out.println(mapId + ":" + mapIdList.get(mapId) + ":" + mapTypeList.get(mapId));
			}
		} catch (IOException exception) {
			//File isn't found, oh well
		}
	}

	private ItemStack getMap(Player player, String name, String type) {
		ItemStack m = new ItemStack(Material.EMPTY_MAP);
		if (type.equals("url")){
			File f = new File(getFileName(name.split("/")[name.split("/").length - 1].split(".png")[0]));
			if (!f.exists()) downloadURL(name);
			m = new ItemStack(Material.MAP);
			MapView mv = this.plugin.getServer().createMap(this.plugin.getServer().getWorlds().get(0));
			mv.setCenterX(MAGIC_NUMBER);
			mv.setCenterZ(0);
			for (MapRenderer mr : mv.getRenderers()) {
				mv.removeRenderer(mr);
			}
			mv.addRenderer(new Render(getFileName(name.split("/")[name.split("/").length - 1].split(".png")[0]), type));
			ItemMeta im = m.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + name.split("/")[name.split("/").length - 1].split(".png")[0]);
			m.setItemMeta(im);
			m.setDurability(mv.getId());
			mapIdList.put(mv.getId(), name.split("/")[name.split("/").length - 1].split(".png")[0]);
			mapTypeList.put(mv.getId(), type);
			player.sendMap(mv);
		} else {
			String fileName;
			fileName = getFileName(name);
			File f = new File(fileName);
			if (!f.exists()) {
				player.sendMessage(ChatColor.GRAY + "Please enter a player's name who exists");
			} else {
				m = new ItemStack(Material.MAP);
				MapView mv = this.plugin.getServer().createMap(this.plugin.getServer().getWorlds().get(0));
				mv.setCenterX(MAGIC_NUMBER);
				mv.setCenterZ(0);
				for (MapRenderer mr : mv.getRenderers()) {
					mv.removeRenderer(mr);
				}
				mv.addRenderer(new Render(fileName, type));
				ItemMeta im = m.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + name);
				m.setItemMeta(im);
				m.setDurability(mv.getId());
				mapIdList.put(mv.getId(), name);
				mapTypeList.put(mv.getId(), type);
				player.sendMap(mv);
			}
		}
		return m;
	}

	private static void chkFolder(File f, String t) {
		if (!f.exists()) {
			try {
				if (t.equals("d")) {
					if (!f.mkdir()) {
						System.out.println("Failed to create directory " + f.getAbsolutePath());
					}
				} else if (t.equals("f")) {
					if (!f.createNewFile()) {
						System.out.println("Failed to create file " + f.getAbsolutePath());
					}
				}
			} catch (Exception exception) {
				//Problem making folder or file
			}
		}
	}

	private void createDirStucture() {
		chkFolder(this.pluginFolder, "d");
		chkFolder(this.cacheFolder, "d");
	}
}