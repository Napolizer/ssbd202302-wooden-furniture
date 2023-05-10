package pl.lodz.p.it.ssbd2023.ssbd02.utils.security;


import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.impl.Base64Codec;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public final class CryptHashUtils {
  private static final String SALT;
  private static final int BCRYPT_COST_FACTOR;

  static {
    Properties prop = new Properties();
    try (InputStream input = CryptHashUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      SALT = prop.getProperty("hash.salt");
      BCRYPT_COST_FACTOR = Integer.parseInt(prop.getProperty("hash.cost.factor"));
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

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
