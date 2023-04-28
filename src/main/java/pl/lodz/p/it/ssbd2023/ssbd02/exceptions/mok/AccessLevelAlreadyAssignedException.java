package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccessLevelAlreadyAssignedException extends BaseWebApplicationException {
    public AccessLevelAlreadyAssignedException() {
        super(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED, Response.Status.BAD_REQUEST);
    }
}
