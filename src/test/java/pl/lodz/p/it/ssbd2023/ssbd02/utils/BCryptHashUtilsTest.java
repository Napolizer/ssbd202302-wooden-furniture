package pl.lodz.p.it.ssbd2023.ssbd02.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.BCryptHashUtils;

public class BCryptHashUtilsTest {
    @Test
    public void properlyGeneratesHashPassword() {
        String password = "test123!";
        Assertions.assertNotNull(BCryptHashUtils.hashPassword(password));
    }

    @Test
    public void properlyVerifiesTwoIdenticalPasswords() {
        String password = "test123!";
        String hashed = BCryptHashUtils.hashPassword(password);
        Assertions.assertEquals(true, BCryptHashUtils.verifyPassword(password, hashed));
    }

    @Test
    public void properlyGeneratesDifferentHashesFromSamesPassword() {
        String password = "test123!";
        Assertions.assertNotEquals(BCryptHashUtils.hashPassword(password), BCryptHashUtils.hashPassword(password));
    }

    @Test
    public void properlyVerifiesTwoDifferentPasswords() {
        String password1 = "test123";
        String password2 = "test123!";
        String hashed = BCryptHashUtils.hashPassword(password2);
        Assertions.assertEquals(false, BCryptHashUtils.verifyPassword(password1, hashed));
    }

    @Test
    public void properlyGeneratesHashVersion() {
        Long version = 6L;
        Assertions.assertNotNull(BCryptHashUtils.hashVersion(version));
    }

    @Test
    public void properlyVerifiesTwoSameVersions() {
        Long version = 6L;
        String hashed = BCryptHashUtils.hashVersion(version);
        Assertions.assertEquals(true, BCryptHashUtils.verifyVersion(version, hashed));
    }

    @Test
    public void properlyVerifiesTwoDifferentVersions() {
        Long version1 = 6L;
        Long version2 = 7L;
        String hashed = BCryptHashUtils.hashVersion(version2);
        Assertions.assertEquals(false, BCryptHashUtils.verifyVersion(version1, hashed));
    }

}
