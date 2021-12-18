package dev.bodewig.saruman.server;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.security.auth.DestroyFailedException;

import com.codedisaster.steamworks.SteamID;

public class SarumanServer {
	private static final String DATA_SEPARATOR = "~>";

	public static final String DEFAULT_KEY_PATH = "saruman_private.key";

	public static String generateUnlockCode(SteamID userId, String achievementName, String keyPath)
			throws EncryptionException, ReadKeyException {
		if (userId == null) {
			throw new IllegalArgumentException("User id cannot be null");
		}
		PrivateKey privateKey = readPrivateKey(keyPath);
		String data = String.valueOf(userId.getAccountID()) + DATA_SEPARATOR + achievementName;
		return encryptData(data, privateKey);
	}

	public static String generateUnlockCode(SteamID userId, String achievementName)
			throws EncryptionException, ReadKeyException {
		return generateUnlockCode(userId, achievementName, DEFAULT_KEY_PATH);
	}

	public static String generateGeneralUnlockCode(String achievementName, String keyPath)
			throws EncryptionException, ReadKeyException {
		PrivateKey privateKey = readPrivateKey(keyPath);
		String data = DATA_SEPARATOR + achievementName;
		return encryptData(data, privateKey);

	}

	public static String generateGeneralUnlockCode(String achievementName)
			throws EncryptionException, ReadKeyException {
		return generateGeneralUnlockCode(achievementName, DEFAULT_KEY_PATH);
	}

	private static PrivateKey readPrivateKey(String keyPath) throws ReadKeyException {
		try {
			InputStream stream = SarumanServer.class.getClassLoader().getResourceAsStream(keyPath);
			byte[] keyBytes = stream.readAllBytes();
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			throw new ReadKeyException(e);
		}
	}

	private static String encryptData(String data, PrivateKey privateKey) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] encryptedBytes = cipher.doFinal(dataBytes);
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			throw new EncryptionException(e);
		} finally {
			try {
				privateKey.destroy();
			} catch (DestroyFailedException dfe) {
				// ignore?
			}
		}
	}

}
