package com.amentrix.evilbook.nametag;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.Bukkit;

class PackageChecker
{
	private static String version = "";

	public static String getVersion()
	{
		return version;
	}

	static
	{
		try
		{
			File file = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null)
				{
					String name = entry.getName().replace("\\", "/");

					if (name.startsWith("org/bukkit/craftbukkit/v")) {
						String ver = "";
						for (int t = "org/bukkit/craftbukkit/v".length(); t < name.length(); t++) {
							char c = name.charAt(t);
							if (c == '/') break;
							ver = ver + c;
						}

						version = "v" + ver;
						break;
					}
				}

				zis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Could not locate craftbukkit's package version (you're probably going to have a lot of errors after this!)");

			e.printStackTrace();
		}
	}
}