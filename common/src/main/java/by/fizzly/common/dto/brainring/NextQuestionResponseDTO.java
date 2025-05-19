package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "DTO для ответа о следующем вопросе")
public class NextQuestionResponseDTO {
    @Schema(description = "Идентификатор события")
    private Long eventId;
    
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Флаг готовности к следующему вопросу")
    private boolean ready;
}
