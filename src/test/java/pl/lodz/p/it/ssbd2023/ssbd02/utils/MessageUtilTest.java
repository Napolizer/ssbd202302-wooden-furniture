package pl.lodz.p.it.ssbd2023.ssbd02.utils;

import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageUtilTest {

    @Test
    void shouldReturnMessageInEnglish() {
        String message = MessageUtil.getMessage("en",
                MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT);

        assertEquals("Account blocked", message);
    }

    @Test
    void shouldReturnMessageInPolish() {
        String message = MessageUtil.getMessage("pl",
                MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT);

        assertEquals("Konto zablokowane", message);
    }

    @Test
    void shouldReturnMessageInEnglishIfThereIsNoLanguageAvailable() {
        String message = MessageUtil.getMessage("fr",
                MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT);

        assertEquals("Account blocked", message);
    }
}
