package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper;

import jakarta.ejb.Stateful;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TimeZone;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateful
public class AccountMapper {
  @Inject
  private AddressMapper addressMapper;

  @Inject
  private AccountServiceOperations accountService;

  public AccountWithoutSensitiveDataDto mapToAccountWithoutSensitiveDataDto(Account account) {
    return AccountWithoutSensitiveDataDto.builder()
            .id(account.getId())
            .login(account.getLogin())
            .email(account.getEmail())
            .firstName(account.getPerson().getFirstName())
            .lastName(account.getPerson().getLastName())
            .archive(account.getArchive())
            .lastLoginIpAddress(account.getLastLoginIpAddress())
            .lastFailedLoginIpAddress(account.getLastFailedLoginIpAddress())
            .locale(account.getLocale())
            .failedLoginCounter(account.getFailedLoginCounter())
            .blockadeEnd(account.getBlockadeEnd())
            .accountState(account.getAccountState())
            .roles(account.getAccessLevels()
                    .stream()
                    .map(AccessLevel::getRoleName)
                    .toList())
            .address(addressMapper.mapToAddressDto(account.getPerson().getAddress()))
            .hash(CryptHashUtils.hashVersion(account.getSumOfVersions()))
            .lastLogin(account.getLastLogin())
            .lastFailedLogin(account.getLastFailedLogin())
            .timeZone(account.getTimeZone().getDisplayName())
            .build();
  }

  public AccountWithoutSensitiveDataDto
      mapToAccountWithoutSensitiveDataWithTimezone(Account account) {

    AccountWithoutSensitiveDataDto mapped = mapToAccountWithoutSensitiveDataDto(account);
    String login = CDI.current().select(Principal.class).get().getName();
    Account principal = accountService.getAccountByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    mapped.setLastLogin(mapTimeToNewZone(account.getLastLogin(), principal.getTimeZone().getDisplayName()));
    mapped.setLastFailedLogin(mapTimeToNewZone(account.getLastFailedLogin(), principal.getTimeZone().getDisplayName()));
    mapped.setBlockadeEnd(mapTimeToNewZone(account.getBlockadeEnd(), principal.getTimeZone().getDisplayName()));

    return mapped;
  }

  public LocalDateTime mapTimeToNewZone(LocalDateTime oldDateTime, String zone) {
    if (oldDateTime != null) {
      ZoneId oldZone = ZoneId.of(TimeZone.EUROPE_WARSAW.getDisplayName());
      ZoneId newZone = ZoneId.of(zone);

      return oldDateTime.atZone(oldZone)
              .withZoneSameInstant(newZone)
              .toLocalDateTime();
    }
    return null;
  }
}
