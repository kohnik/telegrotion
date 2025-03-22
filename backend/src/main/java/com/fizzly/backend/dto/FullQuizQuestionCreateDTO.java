package com.fizzly.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для создания полного квиза (DTO вопрос)")
public class FullQuizQuestionCreateDTO {

    @Schema(description = "Название вопроса")
    private String question;

    @Schema(description = "Кол-во очков за правильный ответ")
    private int points;

    @Schema(description = "Список ответов")
    private List<FullQuizAnswerCreateDTO> answers;
}
