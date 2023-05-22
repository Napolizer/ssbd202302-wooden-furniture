package pl.lodz.p.it.ssbd2023.ssbd02.entities;

public enum TimeZone {
  EUROPE_WARSAW("Europe/Warsaw"),
  AMERICA_NEW_YORK("America/New_York"),
  AMERICA_LOS_ANGELES("America/Los_Angeles"),
  ASIA_TOKYO("Asia/Tokyo"),
  AUSTRALIA_SYDNEY("Australia/Sydney"),
  EUROPE_LONDON("Europe/London"),
  EUROPE_BERLIN("Europe/Berlin"),
  AMERICA_CHICAGO("America/Chicago"),
  ASIA_SHANGHAI("Asia/Shanghai"),
  AMERICA_SAO_PAULO("America/Sao_Paulo");

  private final String name;

  TimeZone(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return name;
  }
}
