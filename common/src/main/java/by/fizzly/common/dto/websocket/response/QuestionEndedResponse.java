package by.fizzly.common.dto.websocket.response;

import by.fizzly.common.dto.quiz.PlayerSubmittedAnswer;
import by.fizzly.common.dto.websocket.QuestionEndedPlayerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для ответа о завершении вопроса")
public class QuestionEndedResponse {
    private Long eventId;
    private int correctAnswer;
    @Schema(description = "Список игроков с их результатами")
    private List<QuestionEndedPlayerDTO> players;
    private List<PlayerSubmittedAnswer> answers;
}
