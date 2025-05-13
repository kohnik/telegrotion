package by.fizzly.common.dto.quiz;

public enum QuestionType {

    STANDARD("Standard"),
    SLIDER("Slider"),
    TF("True/False");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
