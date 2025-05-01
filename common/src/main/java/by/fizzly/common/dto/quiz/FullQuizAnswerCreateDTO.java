package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для создания полного квиза (DTO для ответа)")
public class FullQuizAnswerCreateDTO {

    @Schema(description = "Название вопроса")
    private String answer;

    @Schema(description = "Правильный или неправильный ответ")
    private boolean correct;

    @Schema(description = "Порядковый номер ответа")
    private int order;
}
