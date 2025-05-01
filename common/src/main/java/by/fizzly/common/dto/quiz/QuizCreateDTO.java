package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для создания квиза")
public class QuizCreateDTO {

    @Schema(description = "Название квиза")
    private String name;

    @Schema(description = "ИД пользователя который создает квиз")
    private Long userId;
}
