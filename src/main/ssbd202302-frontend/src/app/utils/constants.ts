export class Constants {
  public static readonly REFRESH_TOKEN_TIME = 180000;
  public static readonly LOGIN_PATTERN = '^[a-zA-Z][a-zA-Z0-9]*$';
  public static readonly PASSWORD_PATTERN = '^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$';
  public static readonly CAPITALIZED_PATTERN =
    '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$';
  public static readonly POSTAL_CODE_PATTERN = '[0-9]{2}-[0-9]{3}';
  public static readonly COMPANY_PATTERN =
    '^[A-ZĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s\\d.-]*$';
}
