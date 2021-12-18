package dev.bodewig.saruman.test;

import com.codedisaster.steamworks.SteamID;
import dev.bodewig.saruman.server.EncryptionException;
import dev.bodewig.saruman.server.ReadKeyException;
import dev.bodewig.saruman.server.SarumanServer;

public class Server {

  public static void main(String[] args) {
    staticContext();
  }

  public static void staticContext() {
    SteamID userId = new SteamID();
    String name = "achievementName";

    try {
      String unlockCodeForUser = SarumanServer.generateUnlockCode(userId, name);
      // additional argument for custom key file path
      System.out.println(unlockCodeForUser);
    } catch (EncryptionException de) {
      // logic for invalid code
      throw new RuntimeException(de);
    } catch (ReadKeyException rke) {
      // logic for missing key file
      throw new RuntimeException(rke);
    }

    try {
      String generalUnlockCode = SarumanServer.generateGeneralUnlockCode(name);
      // additional argument for custom key file path
      System.out.println(generalUnlockCode);
    } catch (EncryptionException de) {
      // logic for invalid code
      throw new RuntimeException(de);
    } catch (ReadKeyException rke) {
      // logic for missing key file
      throw new RuntimeException(rke);
    }
  }
}
