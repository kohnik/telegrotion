package by.fizzly.common.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AnswerRequestDTO {
    private UUID roomId;
    private UUID playerId;
    private double answerTime;
}
