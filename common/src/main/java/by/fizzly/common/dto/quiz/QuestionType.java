package by.fizzly.common.dto.quiz;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Типы вопросов в квизе")
public enum QuestionType {
    @Schema(description = "Стандартный вопрос")
    STANDARD("Standard"),
    
    @Schema(description = "Вопрос с ползунком")
    SLIDER("Slider"),
    
    @Schema(description = "Вопрос типа 'Правда/Ложь'")
    TF("True/False");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
