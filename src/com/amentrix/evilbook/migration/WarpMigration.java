package com.amentrix.evilbook.migration;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

public class WarpMigration {
	public static void migrate() {
		EvilBook.logInfo("Converting warp table to new format");
		
		SQL.addColumn(TableType.Warps, "world VARCHAR(64)");
		SQL.addColumn(TableType.Warps, "x DOUBLE");
		SQL.addColumn(TableType.Warps, "y DOUBLE");
		SQL.addColumn(TableType.Warps, "z DOUBLE");
		SQL.addColumn(TableType.Warps, "yaw FLOAT");
		SQL.addColumn(TableType.Warps, "pitch FLOAT");
		
		int errorsOccured = 0;
		
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Warps.getName() + ";")) {
				while (rs.next()) {
					String rawWarp = rs.getString("location");
					String warpName = rs.getString("warp_name").toLowerCase(Locale.UK).replaceAll("'", "''");
					
					SQL.setValue(TableType.Warps, "world", warpName, rawWarp.split(">")[0]);
					SQL.setValue(TableType.Warps, "x", warpName, rawWarp.split(">")[1]);
					SQL.setValue(TableType.Warps, "y", warpName, rawWarp.split(">")[2]);
					SQL.setValue(TableType.Warps, "z", warpName, rawWarp.split(">")[3]);
					SQL.setValue(TableType.Warps, "yaw", warpName, rawWarp.split(">")[4]);
					SQL.setValue(TableType.Warps, "pitch", warpName, rawWarp.split(">")[5]);
				}
			}
		} catch (Exception exception) {
			errorsOccured++;
		}
		
		if (errorsOccured == 0) {
			try (Statement statement = SQL.connection.createStatement()) {
				statement.execute("ALTER TABLE " + SQL.database + "." + TableType.Warps.getName() + " DROP COLUMN location;");
			} catch (Exception e) {
				e.printStackTrace();
			}
			EvilBook.logInfo("Successfully converted warp table to new format");
		} else {
			EvilBook.logSevere(errorsOccured + " errors whilst converting old warp format to new. Please manually delete obsolete location column");
		}
	}
}
