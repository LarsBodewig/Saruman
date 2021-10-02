package dev.bodewig.saruman.client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUserStats;

public class SarumanClient {
	private static final String DATA_SEPARATOR = "~>";

	public static final String DEFAULT_KEY_PATH = "saruman_public.key";

	private final SteamUserStats userStats;
	private final PublicKey publicKey;
	private final SteamID userId;

	public static void unlockAchievement(SteamID userId, SteamUserStats userStats, String code, String path)
			throws DecryptionException, ReadKeyException {
		PublicKey publicKey = readPublicKey(path);
		unlockAchievement(userId, userStats, code, publicKey);
	}

	public static void unlockAchievement(SteamID userId, SteamUserStats userStats, String code)
			throws DecryptionException, ReadKeyException {
		unlockAchievement(userId, userStats, code, DEFAULT_KEY_PATH);
	}

	public static void lockAchievement(SteamUserStats userStats, String name) {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		userStats.clearAchievement(name);
	}

	public SarumanClient(SteamID id, SteamUserStats userStats) throws ReadKeyException {
		this(id, userStats, DEFAULT_KEY_PATH);
	}

	public SarumanClient(SteamID userId, SteamUserStats userStats, String keyPath) throws ReadKeyException {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		this.userId = userId;
		this.userStats = userStats;
		this.publicKey = readPublicKey(keyPath);
	}

	public void unlockAchievement(String code) throws DecryptionException {
		unlockAchievement(this.userId, this.userStats, code, this.publicKey);
	}

	public void lockAchievement(String name) {
		lockAchievement(this.userStats, name);
	}

	private static PublicKey readPublicKey(String path) throws ReadKeyException {
		try {
			InputStream stream = SarumanClient.class.getClassLoader().getResourceAsStream(path);
			byte[] keyBytes = stream.readAllBytes();
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new ReadKeyException(e);
		}
	}

	private static void unlockAchievement(SteamID userId, SteamUserStats userStats, String code, PublicKey publicKey)
			throws DecryptionException {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		String data = decryptAchievement(code, publicKey);
		String name = getAchievementNameFromCode(userId, data);
		userStats.setAchievement(name);
	}

	private static String decryptAchievement(String code, PublicKey publicKey) throws DecryptionException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] encryptedBytes = code.getBytes(StandardCharsets.UTF_8);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
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
