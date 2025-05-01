package by.fizzly.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для создания пользователя")
public class UserCreateDTO {

    @Schema(description = "Юзернейм пользователя")
    private String username;

}
