package com.fizzly.backend.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для получения списка квизов")
public class GetListQuizDTO {

    @Schema(description = "ИД квиза")
    private Long quizId;

    @Schema(description = "Название квиза")
    private String name;

    @Schema(description = "Никнейм пользователя, который создал квиз")
    private String username;

    @Schema(description = "Кол-во вопросов в квизе")
    private int questionCount;
}
