package by.fizzly.common.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NextQuestionResponseDTO {
    private Long eventId;
    private UUID roomId;
    private boolean ready;
}
