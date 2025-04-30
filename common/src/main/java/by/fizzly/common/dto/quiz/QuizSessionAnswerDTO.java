package by.fizzly.common.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSessionAnswerDTO implements Serializable {
    private String answer;
    private int order;
    private boolean isCorrect;
}
