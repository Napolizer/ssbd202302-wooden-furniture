package pl.lodz.p.it.ssbd2023.ssbd02.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;

public class InitData {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String mapToJsonString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
  }

  public static AccountRegisterDto getAccountToRegister() {
    return AccountRegisterDto.builder()
            .firstName("Thomas")
            .lastName("Coe")
            .country("Germany")
            .city("Berlin")
            .street("Street")
            .streetNumber(34)
            .postalCode("93-540")
            .password("Password123!")
            .locale("pl")
            .login("Register123")
            .email("register123@example.com")
            .build();
  }

  public static AccountRegisterDto getAccountWithCompany() {
    return AccountRegisterDto.builder()
            .firstName("John")
            .lastName("Coe")
            .country("Germany")
            .city("Berlin")
            .street("Street")
            .streetNumber(34)
            .postalCode("93-540")
            .password("Password123!")
            .locale("pl")
            .login("Company123")
            .email("company123@example.com")
            .nip("1111111111")
            .companyName("New Company")
            .build();
  }

  public static AccountCreateDto getAccountToCreate() {
    return AccountCreateDto.builder()
            .firstName("John")
            .lastName("Boe")
            .country("Poland")
            .city("Lodz")
            .street("Karpacka")
            .streetNumber(55)
            .postalCode("93-539")
            .password("Password123!")
            .locale("pl")
            .accessLevel(new AccessLevelDto("administrator"))
            .login("active123")
            .email("active123@example.com")
            .nip("1111111111")
            .companyName("Example Company")
            .build();
  }

  public static String accountToEditAccessLevelsJson = """
          {
              "firstName": "Thomas",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Employee"
                  }
              ],
              "login": "accounttoeditals",
              "email": "toeditaccesslevel@example.com"
          }
      """;


  public static String accountToEditJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "accounttoedit123",
              "email": "toedit@example.com"
          }
      """;

  public static String editedAccountExampleJson = """
      {
        "firstName": "Adam",
        "lastName": "Doe",
        "country": "USA",
        "city": "Warsaw",
        "street": "Wladyslawa",
        "postalCode": "95-200",
        "streetNumber": 20,
        "hash": "$hash"
      }
      """;

  public static String editedAccountAsAdminExampleJson = """
      {
        "firstName": "John",
        "lastName": "Doe",
        "country": "United States",
        "city": "New York",
        "street": "Broadway",
        "postalCode": "10-001",
        "streetNumber": 123,
        "hash": "$hash"
      }
      """;

}
