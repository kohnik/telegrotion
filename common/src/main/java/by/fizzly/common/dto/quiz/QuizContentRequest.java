package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для запроса содержимого квиза")
public class QuizContentRequest {
    @Schema(description = "Идентификатор квиза")
    private Long quizId;
    
    @Schema(description = "Идентификатор вопроса")
    private Long questionId;
}
