package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для комнаты сессии квиза")
public class QuizSessionRoom implements Serializable {
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
    
    @Schema(description = "Флаг активности комнаты")
    private boolean active;
    
    @Schema(description = "Идентификатор квиза")
    private Long quizId;
}
