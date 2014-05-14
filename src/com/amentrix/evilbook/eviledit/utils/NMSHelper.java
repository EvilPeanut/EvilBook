package com.amentrix.evilbook.eviledit.utils;

import com.amentrix.evilbook.main.EvilBook;

public class NMSHelper {
	private static NMSAbstraction nms = null;

	public static NMSAbstraction init() {
		try {
			nms = NMSHandler.class.getConstructor().newInstance();
		} catch (Exception e) {
			EvilBook.logSevere("Failed to create EvilEdit engine NMSAbstraction instance");
		}
		return nms;
	}

	public static NMSAbstraction getNMS() {
		return nms;
	}
}