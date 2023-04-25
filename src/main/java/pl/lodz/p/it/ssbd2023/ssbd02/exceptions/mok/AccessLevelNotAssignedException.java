package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccessLevelNotAssignedException extends Exception {
    public AccessLevelNotAssignedException() {
        super(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL_NOT_ASSIGNED);
    }
}
