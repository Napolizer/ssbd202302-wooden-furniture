package pl.lodz.p.it.ssbd2023.ssbd02.web.listener;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.util.List;

@WebListener
public class StartupListener implements ServletContextListener {
    @Inject
    private PersonFacadeOperations personFacadeOperations;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (personFacadeOperations.findByAccountLogin("admin").isPresent()) {
            return;
        }

        Person admin = Person.builder()
                .firstName("Root")
                .lastName("Administrator")
                .address(Address.builder()
                        .country("UK")
                        .city("London")
                        .street("Big")
                        .postalCode("90-000")
                        .streetNumber(12)
                        .build())
                .account(Account.builder()
                        .login("admin")
                        .password("kochamssbd")
                        .email("admin@gmail.com")
                        .locale("pl")
                        .accountState(AccountState.ACTIVE)
                        .accessLevels(List.of(new Administrator()))
                        .build())
                .build();
        personFacadeOperations.create(admin);
    }
}
