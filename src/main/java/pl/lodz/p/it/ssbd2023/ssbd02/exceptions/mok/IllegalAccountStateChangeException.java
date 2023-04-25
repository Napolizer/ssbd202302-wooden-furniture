package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class IllegalAccountStateChangeException extends Exception {
    public IllegalAccountStateChangeException() {
        super(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE);
    }
}
