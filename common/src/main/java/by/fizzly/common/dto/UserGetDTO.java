package by.fizzly.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для получения пользователя")
public class UserGetDTO {

    @Schema(description = "Юзернейм пользователя")
    private String username;

}
