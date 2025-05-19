package by.fizzly.backend.mapper;

import by.fizzly.common.dto.quiz.QuizCreateDTO;
import by.fizzly.backend.entity.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);
}
