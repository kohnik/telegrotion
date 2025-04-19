package com.fizzly.backend.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для добавления нескольких ответов на вопрос к квизу")
public class QuizAnswerMultipleCreateDTO {

    @Schema(description = "Список вопросов")
    private List<QuizAnswerCreateDTO> answers;

    @Schema(description = "ИД вопроса")
    private Long questionId;
}
