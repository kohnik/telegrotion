package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO для ответа игрока BrainRing с событием")
public class AnswerResponseWithEventDTO extends AnswerResponseDTO {
    @Schema(description = "Идентификатор события")
    private Long eventId;

    public AnswerResponseWithEventDTO(Long eventId, UUID playerId, String teamName, double answerTime) {
        super(playerId, teamName, answerTime);
        this.eventId = eventId;
    }
}
