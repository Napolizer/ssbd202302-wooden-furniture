package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security;

import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccountArchiveException extends AuthenticationException {
    public AccountArchiveException() {
        super(MessageUtil.MessageKey.ACCOUNT_ARCHIVE);
    }
}
