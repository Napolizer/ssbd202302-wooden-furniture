package pl.lodz.p.it.ssbd2023.ssbd02.utils.security;


import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.impl.Base64Codec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CryptHashUtils {

  //TODO read from file
  private static final String SALT = "+a+fz)tT3m&}X@Wh";
  private static final int BCRYPT_COST_FACTOR = 12;

  public static String hashPassword(String password) {
    return BCrypt.withDefaults().hashToString(BCRYPT_COST_FACTOR, password.toCharArray());
  }

  public static boolean verifyPassword(String password, String hash) {
    return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified;
  }

  public static String hashVersion(Long version) {
    return BCrypt.withDefaults().hashToString(BCRYPT_COST_FACTOR, Long.toString(version).toCharArray());
  }

  public static boolean verifyVersion(Long version, String hash) {
    return BCrypt.verifyer()
        .verify(Long.toString(version).toCharArray(), hash.toCharArray()).verified;
  }

  public static String getSecretKeyForEmailToken(String input) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(SALT.getBytes());
    return new Base64Codec().encode(md.digest(input.getBytes()));
  }
}
