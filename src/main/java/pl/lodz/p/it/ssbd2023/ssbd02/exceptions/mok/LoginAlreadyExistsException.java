package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class LoginAlreadyExistsException extends Exception {
    public LoginAlreadyExistsException() {
        super(MessageUtil.MessageKey.ACCOUNT_LOGIN);
    }
}
