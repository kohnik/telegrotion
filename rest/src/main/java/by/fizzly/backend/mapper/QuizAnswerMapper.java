package by.fizzly.backend.mapper;

import by.fizzly.common.dto.quiz.QuizAnswerCreateDTO;
import by.fizzly.backend.entity.QuizAnswer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizAnswerMapper {

    QuizAnswer toQuizAnswer(QuizAnswerCreateDTO createDTO);

    List<QuizAnswer> toQuizAnswerList(List<QuizAnswerCreateDTO> answers);
}
