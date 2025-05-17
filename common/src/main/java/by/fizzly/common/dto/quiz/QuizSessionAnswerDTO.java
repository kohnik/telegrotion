package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для ответа в сессии квиза")
public class QuizSessionAnswerDTO implements Serializable {
    @Schema(description = "Текст ответа")
    private String answer;
    
    @Schema(description = "Порядковый номер ответа")
    private int order;
    
    @Schema(description = "Флаг правильности ответа")
    private boolean isCorrect;
}
