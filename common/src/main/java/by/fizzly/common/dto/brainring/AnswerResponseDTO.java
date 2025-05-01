package by.fizzly.common.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponseDTO {
    private UUID playerId;
    private String playerName;
    private double answerTime;
}
