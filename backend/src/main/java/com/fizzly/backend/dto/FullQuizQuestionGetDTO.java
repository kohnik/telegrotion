package com.fizzly.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для получения полного квиза (DTO вопрос)")
public class FullQuizQuestionGetDTO {

    @Schema(description = "ИД вопроса")
    private Long questionId;

    @Schema(description = "Название вопроса")
    private String question;

    @Schema(description = "Кол-во очков за правильный ответ")
    private int points;

    @Schema(description = "Порядковый номер вопроса")
    private int order;

    @Schema(description = "Время ответа на вопрос")
    private int seconds;

    @Schema(description = "Список ответов")
    private List<FullQuizAnswerGetDTO> answers;
}
