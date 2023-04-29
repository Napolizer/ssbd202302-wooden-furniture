package pl.lodz.p.it.ssbd2023.ssbd02.utils.security;


import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public final class CryptHashUtils {

  //TODO read secret key from file
  private static final String secretKey = "randomSecretKey";
  private static final int BCRYPT_COST_FACTOR = 12;
  private static final SecureRandom secureRandom =
      new SecureRandom(secretKey.getBytes(StandardCharsets.UTF_8));

  public static String hashPassword(String password) {
    return BCrypt.with(secureRandom).hashToString(BCRYPT_COST_FACTOR, password.toCharArray());
  }

  public static boolean verifyPassword(String password, String hash) {
    return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified;
  }

  public static String hashVersion(Long version) {
    return BCrypt.with(secureRandom)
        .hashToString(BCRYPT_COST_FACTOR, Long.toString(version).toCharArray());
  }

  public static boolean verifyVersion(Long version, String hash) {
    return BCrypt.verifyer()
        .verify(Long.toString(version).toCharArray(), hash.toCharArray()).verified;
  }

}
