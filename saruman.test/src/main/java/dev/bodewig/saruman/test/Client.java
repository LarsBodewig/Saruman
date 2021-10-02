package dev.bodewig.saruman.test;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;

import dev.bodewig.saruman.client.DecryptionException;
import dev.bodewig.saruman.client.ReadKeyException;
import dev.bodewig.saruman.client.SarumanClient;
import dev.bodewig.saruman.client.SteamUserStatsCallbackAdapter;

public class Client {

	public static void main(String[] args) {
		objectContext();
		staticContext();
	}

	public static void objectContext() {
		SteamUserStatsCallback adapter = new SteamUserStatsCallbackAdapter();
		SteamUserStats userStats = new SteamUserStats(adapter);
		SteamID userId = new SteamID();

		SarumanClient saruman;
		try {
			saruman = new SarumanClient(userId, userStats); // additional argument for custom key file path
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
		SteamID userId = new SteamID();

		String code = "unlockCode";
		try {
			SarumanClient.unlockAchievement(userId, userStats, code); // additional argument for custom key file path
		} catch (DecryptionException de) {
			// logic for invalid code
			throw new RuntimeException(de);
		} catch (ReadKeyException rke) {
			// logic for missing key file
			throw new RuntimeException(rke);
		}

		String name = "achievementName";
		SarumanClient.lockAchievement(userStats, name);
	}
}
