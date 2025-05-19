package by.fizzly.backend.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа аутентификации")
public class AuthResponseDto {
    @Schema(description = "JWT токен доступа")
    private String accessToken;
    
    @Schema(description = "JWT токен обновления")
    private String refreshToken;
    
    @Schema(description = "UUID для обновления токена")
    private UUID refreshUUID;
}
