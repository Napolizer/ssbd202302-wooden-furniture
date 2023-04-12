package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class AccountCreateDto extends AccountRegisterDto {

    @NotNull
    private AccountState accountState;

    @NotNull
    private List<AccessLevelDto> accessLevels;
}