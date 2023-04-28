package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class IllegalAccountStateChangeException extends BaseWebApplicationException {
    public IllegalAccountStateChangeException() {
        super(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE, Response.Status.BAD_REQUEST);
    }
}
