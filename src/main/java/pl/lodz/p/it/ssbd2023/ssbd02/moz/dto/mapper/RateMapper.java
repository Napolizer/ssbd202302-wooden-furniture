package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;

public final class RateMapper {

  public static RateDto matToRateDto(Rate rate) {
    return new RateDto(rate.getValue());
  }
}
