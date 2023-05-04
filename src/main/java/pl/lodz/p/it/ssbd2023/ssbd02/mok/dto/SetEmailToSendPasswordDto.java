package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SetEmailToSendPasswordDto {
  @Email
  private String email;
}
