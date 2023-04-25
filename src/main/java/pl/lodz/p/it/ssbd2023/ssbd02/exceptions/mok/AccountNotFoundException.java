package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException() {
        super(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND);
    }
}
