package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Stateless
public class AccountMapper {
  @Inject
  private AddressMapper addressMapper;

  public AccountWithoutSensitiveDataDto mapToAccountWithoutSensitiveDataDto(Account account) {
    return AccountWithoutSensitiveDataDto.builder()
        .id(account.getId())
        .login(account.getLogin())
        .email(account.getEmail())
        .firstName(account.getPerson().getFirstName())
        .lastName(account.getPerson().getLastName())
        .archive(account.getArchive())
        .lastLogin(account.getLastLogin())
        .lastFailedLogin(account.getLastFailedLogin())
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
        .build();
  }

  public LocalDateTime mapTimeToNewZone(LocalDateTime oldDateTime, String zone) {
    if (oldDateTime != null) {
      ZoneId oldZone = ZoneId.of("Europe/Warsaw");
      ZoneId newZone = ZoneId.of(zone);

      return oldDateTime.atZone(oldZone)
              .withZoneSameInstant(newZone)
              .toLocalDateTime();
    }
    return null;
  }
}
