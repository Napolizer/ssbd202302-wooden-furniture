package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class UnknownException extends BaseWebApplicationException {
    public UnknownException(Throwable cause) {
        super(MessageUtil.MessageKey.UNKNOWN_ERROR, cause, Response.Status.BAD_REQUEST);
    }
}