package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

@ExtendWith(ArquillianExtension.class)
public class PasswordHashServiceIT {

    @Inject
    PasswordHashService passwordHashService;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addPackages(true, "at.favre.lib")
                .addAsResource(new File("src/main/resources/"), "");
    }

    @Test
    public void properlyGeneratesHash() {
        String password = "test123!";
        Assertions.assertNotNull(passwordHashService.hashPassword(password));
    }

    @Test
    public void properlyComparesTwoIdenticalPasswords() {
        String password = "test123!";
        String hashed = passwordHashService.hashPassword(password);
        Assertions.assertEquals(true, passwordHashService.checkPassword(password, hashed));
    }

    @Test
    public void properlyComparesTwoDifferentPasswords() {
        String password1 = "test123";
        String password2 = "test123!";
        String hashed = passwordHashService.hashPassword(password2);
        Assertions.assertEquals(false, passwordHashService.checkPassword(password1, hashed));
    }
}
