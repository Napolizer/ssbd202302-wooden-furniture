package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException() {
        super(MessageUtil.MessageKey.ACCOUNT_EMAIL_ALREADY_EXISTS);
    }
}
