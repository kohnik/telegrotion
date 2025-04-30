package by.fizzly.common.dto.quiz;

import by.fizzly.common.event.QuizEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSessionDTO implements Serializable {
    private Long eventId = QuizEvent.NEW_QUESTION.getId();
    private Long questionId;
    private String questionName;
    private List<QuizSessionAnswerDTO> answers;
    private int timeLeft;
    private int points;
    private boolean isActive;
    private boolean isNext;
    private boolean isLast;
}
