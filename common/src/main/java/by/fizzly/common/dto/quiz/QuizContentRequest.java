package by.fizzly.common.dto.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizContentRequest {
    private Long quizId;
    private Long questionId;
}
