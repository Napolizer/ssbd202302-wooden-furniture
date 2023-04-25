package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccessLevelAlreadyAssignedException extends Exception {
    public AccessLevelAlreadyAssignedException() {
        super(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED);
    }
}
