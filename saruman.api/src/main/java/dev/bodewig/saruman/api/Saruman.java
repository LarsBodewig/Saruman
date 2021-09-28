package dev.bodewig.saruman.api;

import com.codedisaster.steamworks.SteamUserStats;

public class Saruman {
	private final SteamUserStats userStats;
	private final String publicKey;

	public Saruman(SteamUserStats userStats) {
		if (userStats == null) {
			throw new RuntimeException("User stats cannot be null");
		}
		this.userStats = userStats;
		this.publicKey = readPublicKey();
	}

	public void unlockAchievement(String code) {
		String name = decryptAchievement(code);
		this.userStats.setAchievement(name);
	}

	public void lockAchievement(String name) {
		this.userStats.clearAchievement(name);
	}

	private String decryptAchievement(String code) {
		// TODO
		return "";
	}

	private static String readPublicKey() {
		// TODO
		return "";
	}
}
