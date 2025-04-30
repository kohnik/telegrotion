package by.fizzly.common.dto.quiz;

import by.fizzly.common.dto.UserGetDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для получения полного квиза")
public class FullQuizGetDTO {

    @Schema(description = "Название квиза")
    private String name;

    @Schema(description = "Список вопросов")
    private List<FullQuizQuestionGetDTO> questions;

    @Schema(description = "Пользователя")
    private UserGetDTO user;
}
