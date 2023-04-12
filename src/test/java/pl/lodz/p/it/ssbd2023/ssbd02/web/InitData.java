package pl.lodz.p.it.ssbd2023.ssbd02.web;

public class InitData {

    public static String activeAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "active123",
        "email": "active123@example.com",
        "accountState": "ACTIVE"
    }
""";

    public static String blockedAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "blocked123",
        "email": "blocked123@example.com",
        "accountState": "BLOCKED"
    }
""";

    public static String notVerifiedAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "notverified123",
        "email": "notverified123@example.com",
        "accountState": "NOT_VERIFIED"
    }
""";

    public static String inactiveAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "inactive123",
        "email": "inactive123@example.com",
        "accountState": "INACTIVE"
    }
""";

    public static String sameLoginAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "active123",
        "email": "test123@example.com",
        "accountState": "ACTIVE"
    }
""";

    public static String sameEmailAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Employee"
            }
        ],
        "login": "test123",
        "email": "active123@example.com",
        "accountState": "ACTIVE"
    }
""";

    public static String invalidAccessLevelAccountJson  = """
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
        "accessLevels": [
            {
                "name": "Invalid"
            }
        ],
        "login": "test123",
        "email": "active123@example.com",
        "accountState": "ACTIVE"
    }
""";

}