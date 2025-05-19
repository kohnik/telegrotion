package by.fizzly.common.dto.quiz;

import by.fizzly.common.event.QuizEvent;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO для сессии квиза")
public class QuizSessionDTO implements Serializable {
    @Schema(description = "ID события")
    private Long eventId = QuizEvent.NEW_QUESTION.getId();
    
    @Schema(description = "ID вопроса")
    private Long questionId;
    
    @Schema(description = "Название вопроса")
    private String questionName;
    
    @Schema(description = "Список ответов на вопрос")
    private List<QuizSessionAnswerDTO> answers;
    
    @Schema(description = "Оставшееся время на вопрос")
    private int timeLeft;
    
    @Schema(description = "Очки за вопрос")
    private int points;
    
    @Schema(description = "Флаг активности вопроса")
    private boolean isActive;
    
    @Schema(description = "Флаг следующего вопроса")
    private boolean isNext;
    
    @Schema(description = "Флаг последнего вопроса")
    private boolean isLast;
}
