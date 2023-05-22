export class TimeZone {
    public static readonly EUROPE_WARSAW = new TimeZone('EUROPE_WARSAW', 'time.zone.europe.warsaw');
    public static readonly AMERICA_NEW_YORK = new TimeZone('AMERICA_NEW_YORK', 'time.zone.america.new_york');
    public static readonly AMERICA_LOS_ANGELES = new TimeZone('AMERICA_LOS_ANGELES', 'time.zone.america.los_angeles');
    public static readonly ASIA_TOKYO = new TimeZone('ASIA_TOKYO', 'time.zone.asia.tokyo');
    public static readonly AUSTRALIA_SYDNEY = new TimeZone('AUSTRALIA_SYDNEY', 'time.zone.australia.sydney');
    public static readonly EUROPE_LONDON = new TimeZone('EUROPE_LONDON', 'time.zone.europe.london');
    public static readonly EUROPE_BERLIN = new TimeZone('EUROPE_BERLIN', 'time.zone.europe.berlin');
    public static readonly AMERICA_CHICAGO = new TimeZone('AMERICA_CHICAGO', 'time.zone.america.chicago');
    public static readonly ASIA_SHANGHAI = new TimeZone('ASIA_SHANGHAI', 'time.zone.asia.shanghai');
    public static readonly AMERICA_SAO_PAULO = new TimeZone('AMERICA_SAO_PAULO', 'time.zone.america.sao_paulo');
  
    private constructor(public readonly value: string, public readonly displayName: string) {}
  }
