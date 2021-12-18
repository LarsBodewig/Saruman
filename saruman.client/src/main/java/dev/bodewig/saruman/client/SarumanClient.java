package dev.bodewig.saruman.client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUserStats;

public class SarumanClient {
	private static final String DATA_SEPARATOR = "~>";

	public static final String DEFAULT_KEY_PATH = "saruman_public.key";

	private final SteamID userId;
	private final SteamUserStats userStats;
	private final PublicKey publicKey;

	public static void unlockAchievement(SteamID userId, SteamUserStats userStats, String unlockCode, String keyPath)
			throws DecryptionException, ReadKeyException {
		PublicKey publicKey = readPublicKey(keyPath);
		unlockAchievement(userId, userStats, unlockCode, publicKey);
	}

	public static void unlockAchievement(SteamID userId, SteamUserStats userStats, String unlockCode)
			throws DecryptionException, ReadKeyException {
		unlockAchievement(userId, userStats, unlockCode, DEFAULT_KEY_PATH);
	}

	public static void lockAchievement(SteamUserStats userStats, String achievementName) {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		userStats.clearAchievement(achievementName);
	}

	public SarumanClient(SteamID userId, SteamUserStats userStats) throws ReadKeyException {
		this(userId, userStats, DEFAULT_KEY_PATH);
	}

	public SarumanClient(SteamID userId, SteamUserStats userStats, String keyPath) throws ReadKeyException {
		if (userId == null) {
			throw new IllegalArgumentException("User id cannot be null");
		}
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		this.userId = userId;
		this.userStats = userStats;
		this.publicKey = readPublicKey(keyPath);
	}

	public void unlockAchievement(String unlockCode) throws DecryptionException {
		unlockAchievement(this.userId, this.userStats, unlockCode, this.publicKey);
	}

	public void lockAchievement(String achievementName) {
		lockAchievement(this.userStats, achievementName);
	}

	private static PublicKey readPublicKey(String keyPath) throws ReadKeyException {
		try {
			InputStream stream = SarumanClient.class.getClassLoader().getResourceAsStream(keyPath);
			byte[] keyBytes = stream.readAllBytes();
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new ReadKeyException(e);
		}
	}

	private static void unlockAchievement(SteamID userId, SteamUserStats userStats, String unlockCode,
			PublicKey publicKey) throws DecryptionException {
		if (userId == null) {
			throw new IllegalArgumentException("User id cannot be null");
		}
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		String data = decryptCode(unlockCode, publicKey);
		String name = getAchievementNameFromCode(userId, data);
		userStats.setAchievement(name);
	}

	private static String decryptCode(String unlockCode, PublicKey publicKey) throws DecryptionException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] decodedBytes = Base64.getDecoder().decode(unlockCode);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new DecryptionException(e);
		}
	}

	private static String getAchievementNameFromCode(SteamID userId, String data) throws DecryptionException {
		String idPrefix = String.valueOf(userId.getAccountID()) + DATA_SEPARATOR;
		int nameIndex;
		if (data.startsWith(idPrefix)) { // user specific unlock code
			nameIndex = idPrefix.length();
		} else if (data.startsWith(DATA_SEPARATOR)) { // general unlock code for all accounts
			nameIndex = DATA_SEPARATOR.length();
		} else {
			throw new DecryptionException("Code is invalid for this account id");
		}
		return data.substring(nameIndex);
	}
}
