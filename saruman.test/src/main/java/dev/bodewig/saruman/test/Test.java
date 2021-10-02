package dev.bodewig.saruman.test;

import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;

import dev.bodewig.saruman.client.DecryptionException;
import dev.bodewig.saruman.client.ReadKeyException;
import dev.bodewig.saruman.client.Saruman;
import dev.bodewig.saruman.client.SteamUserStatsCallbackAdapter;

public class Test {
	public static void objectContext(String[] args) {
		SteamUserStatsCallback adapter = new SteamUserStatsCallbackAdapter();
		SteamUserStats userStats = new SteamUserStats(adapter);

		Saruman saruman;
		try {
			saruman = new Saruman(userStats); // provide second argument for custom key file path
		} catch (ReadKeyException rke) {
			// logic for missing key file
			throw new RuntimeException(rke);
		}

		String code = "unlockCode";
		try {
			saruman.unlockAchievement(code);
		} catch (DecryptionException de) {
			// logic for invalid code
			throw new RuntimeException(de);
		}

		String name = "achievementName";
		saruman.lockAchievement(name);
	}

	public static void staticContext() {
		SteamUserStatsCallback adapter = new SteamUserStatsCallbackAdapter();
		SteamUserStats userStats = new SteamUserStats(adapter);

		String code = "unlockCode";
		try {
			Saruman.unlockAchievement(userStats, code); // provide third argument for custom key file path
		} catch (DecryptionException de) {
			// logic for invalid code
			throw new RuntimeException(de);
		} catch (ReadKeyException rke) {
			// logic for missing key file
			throw new RuntimeException(rke);
		}

		String name = "achievementName";
		Saruman.lockAchievement(userStats, name);
	}
}
