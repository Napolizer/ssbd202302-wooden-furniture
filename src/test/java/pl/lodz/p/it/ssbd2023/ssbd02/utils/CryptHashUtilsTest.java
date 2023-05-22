package pl.lodz.p.it.ssbd2023.ssbd02.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

class CryptHashUtilsTest {
  @Test
  void properlyGeneratesHashPassword() {
    String password = "test123!";
    Assertions.assertNotNull(CryptHashUtils.hashPassword(password));
  }

  @Test
  void properlyVerifiesTwoIdenticalPasswords() {
    String password = "test123!";
    String hashed = CryptHashUtils.hashPassword(password);
      Assertions.assertTrue(CryptHashUtils.verifyPassword(password, hashed));
  }

  @Test
  void properlyGeneratesDifferentHashesFromSamesPassword() {
    String password = "test123!";
    Assertions.assertNotEquals(CryptHashUtils.hashPassword(password), CryptHashUtils.hashPassword(password));
  }

  @Test
  void properlyVerifiesTwoDifferentPasswords() {
    String password1 = "test123";
    String password2 = "test123!";
    String hashed = CryptHashUtils.hashPassword(password2);
      Assertions.assertFalse(CryptHashUtils.verifyPassword(password1, hashed));
  }

  @Test
  void properlyGeneratesHashVersion() {
    Long version = 6L;
    Assertions.assertNotNull(CryptHashUtils.hashVersion(version));
  }

  @Test
  void properlyVerifiesTwoSameVersions() {
    Long version = 6L;
    String hashed = CryptHashUtils.hashVersion(version);
      Assertions.assertTrue(CryptHashUtils.verifyVersion(version, hashed));
  }

  @Test
  void properlyVerifiesTwoDifferentVersions() {
    Long version1 = 6L;
    Long version2 = 7L;
    String hashed = CryptHashUtils.hashVersion(version2);
      Assertions.assertFalse(CryptHashUtils.verifyVersion(version1, hashed));
  }

}
