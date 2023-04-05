package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.security.TokenClaims;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.empty;

public class TokenServiceTest {
    private TokenService tokenService;
    private Account accountAllRoles;
    private Account accountNoRoles;
    private Account administrator;
    private Account client;
    private Account employee;
    private Account salesRep;

    @BeforeEach
    public void setUp() {
        tokenService = new TokenService();
        accountAllRoles = Account.builder()
                                .login("test")
                                .password("test")
                                .email("test@gmail.com")
                                .locale("pl")
                                .accountState(AccountState.ACTIVE)
                                .accessLevels(List.of(
                                        new Administrator(),
                                        new Client(),
                                        new Employee(),
                                        new SalesRep()))
                                .build();
//
//        accountNoRoles = Account.builder()
//                                .login("noroles")
//                                .password("noroles")
//                                .email("noroles@gmail.com")
//                                .locale("pl")
//                                .accountState(AccountState.ACTIVE)
//                                .build();
//        administrator = Account.builder()
//                                .login("admin")
//                                .password("admin")
//                                .email("admin@gmail.com")
//                                .locale("pl")
//                                .accountState(AccountState.ACTIVE)
//                                .accessLevels(List.of(
//                                        AccessLevel.builder()
//                                                .level(AccessLevelName.ADMIN)
//                                                .active(true)
//                                                .build()
//                                        )
//                                )
//                                .build();
//
//        client = Account.builder()
//                                .login("client")
//                                .password("client")
//                                .email("client@gmail.com")
//                                .locale("pl")
//                                .accountState(AccountState.ACTIVE)
//                                .accessLevels(List.of(
//                                        AccessLevel.builder()
//                                                .level(AccessLevelName.CLIENT)
//                                                .active(true)
//                                                .build()
//                                        )
//                                )
//                                .build();
//
//        employee = Account.builder()
//                                .login("employee")
//                                .password("employee")
//                                .email("employee@gmail.com")
//                                .locale("pl")
//                                .accountState(AccountState.ACTIVE)
//                                .accessLevels(List.of(
//                                        AccessLevel.builder()
//                                                .level(AccessLevelName.EMPLOYEE)
//                                                .active(true)
//                                                .build()
//                                        )
//                                )
//                                .build();
//
//        salesRep = Account.builder()
//                                .login("salesRep")
//                                .password("salesRep")
//                                .email("salesRep@gmail.com")
//                                .locale("pl")
//                                .accountState(AccountState.ACTIVE)
//                                .accessLevels(List.of(
//                                        AccessLevel.builder()
//                                                .level(AccessLevelName.SALES_REP)
//                                                .active(true)
//                                                .build()
//                                        )
//                                )
//                                .build();
    }

    @Test
    public void properlyGeneratesTokenTest() {
        String token = tokenService.generateToken(accountAllRoles);
        assertThat(token, is(notNullValue()));
    }
}
