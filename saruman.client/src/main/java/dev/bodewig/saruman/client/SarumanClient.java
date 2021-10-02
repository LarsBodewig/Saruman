package dev.bodewig.saruman.client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.codedisaster.steamworks.SteamUserStats;

public class SarumanClient {
	private static final String DEFAULT_KEY_PATH = "saruman_public.key";

	private final SteamUserStats userStats;
	private final PublicKey publicKey;

	public static void unlockAchievement(SteamUserStats userStats, String code, String path)
			throws DecryptionException, ReadKeyException {
		PublicKey publicKey = readPublicKey(path);
		unlockAchievement(userStats, code, publicKey);
	}

	public static void unlockAchievement(SteamUserStats userStats, String code)
			throws DecryptionException, ReadKeyException {
		unlockAchievement(userStats, code, DEFAULT_KEY_PATH);
	}

	public static void lockAchievement(SteamUserStats userStats, String name) {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		userStats.clearAchievement(name);
	}

	private static void unlockAchievement(SteamUserStats userStats, String code, PublicKey publicKey)
			throws DecryptionException {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		String name = decryptAchievement(code, publicKey);
		userStats.setAchievement(name);
	}

	public SarumanClient(SteamUserStats userStats) throws ReadKeyException {
		this(userStats, DEFAULT_KEY_PATH);
	}

	public SarumanClient(SteamUserStats userStats, String keyPath) throws ReadKeyException {
		if (userStats == null) {
			throw new IllegalArgumentException("User stats cannot be null");
		}
		this.userStats = userStats;
		this.publicKey = readPublicKey(keyPath);
	}

	public void unlockAchievement(String code) throws DecryptionException {
		unlockAchievement(this.userStats, code, this.publicKey);
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
}
