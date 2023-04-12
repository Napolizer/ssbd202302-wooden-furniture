package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.MailService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.File;

@ExtendWith(ArquillianExtension.class)
public class MailServiceIT {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"),"");
    }

    @Inject
    private MailService mailService;

//    @Test
//    void shouldSendMailToTemporaryMail() {
//        assertDoesNotThrow(() -> mailService.sendMailWithInfoAboutBlockingAccount("jegek60138@fectode.com", "en"));
//    }
}
