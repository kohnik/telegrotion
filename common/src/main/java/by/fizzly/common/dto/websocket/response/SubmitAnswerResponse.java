package by.fizzly.common.dto.websocket.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для ответа о отправке ответа")
public class SubmitAnswerResponse {
    @Schema(description = "Идентификатор события")
    private Long eventId;
    
    private UUID playerId;
    private List<String> playersLeft;
}
