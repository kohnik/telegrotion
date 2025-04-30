package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для создания полного квиза")
public class FullQuizCreateDTO {

    @Schema(description = "Название квиза")
    private String name;

    @Schema(description = "Список вопросов")
    private List<FullQuizQuestionCreateDTO> questions;

    @Schema(description = "ИД пользователя")
    private Long userId;
}
