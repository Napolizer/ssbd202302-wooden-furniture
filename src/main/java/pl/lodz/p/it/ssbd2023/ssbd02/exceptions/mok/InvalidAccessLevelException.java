package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class InvalidAccessLevelException extends Exception {
    public InvalidAccessLevelException() {
        super(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL);
    }
}
