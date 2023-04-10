package pl.lodz.p.it.ssbd2023.ssbd02.web.authentication;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.security.TokenClaims;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;

import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class RequestAuthenticationMechanism implements HttpAuthenticationMechanism {
    @Inject
    private TokenService tokenService;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        if (!httpMessageContext.isProtected()) {
            return httpMessageContext.doNothing();
        }

        String authorizationHeader = httpMessageContext.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            try {
                TokenClaims tokenClaims = tokenService.getTokenClaims(token);
                Set<String> groups = tokenClaims.getAccessLevels()
                        .stream()
                        .map(AccessLevel::getGroupName)
                        .collect(Collectors.toSet());
                return httpMessageContext.notifyContainerAboutLogin(tokenClaims.getLogin(), groups);
            } catch (Exception e) {
                return httpMessageContext.responseUnauthorized();
            }
        } else {
            return httpMessageContext.responseUnauthorized();
        }
    }
}
