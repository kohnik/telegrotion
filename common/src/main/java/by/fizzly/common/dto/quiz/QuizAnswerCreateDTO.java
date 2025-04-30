package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для добавления ответа на вопрос к квизу")
public class QuizAnswerCreateDTO {

    @Schema(description = "Название вопроса")
    private String answer;

    @Schema(description = "Правильный или неправильный ответ")
    private boolean correct;

    @Schema(description = "Порядковый номер ответа")
    private int order;

    @Schema(description = "ИД вопроса")
    private Long questionId;
}
