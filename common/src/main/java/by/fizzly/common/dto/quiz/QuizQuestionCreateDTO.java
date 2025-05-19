package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для добавления вопроса к квизу")
public class QuizQuestionCreateDTO {

    @Schema(description = "Название вопроса")
    private String question;

    @Schema(description = "Кол-во очков за правильный ответ")
    private int points;

    @Schema(description = "ИД квиза")
    private Long quizId;

    @Schema(description = "Порядковый номер вопроса")
    private int order;
}
