package com.fizzly.backend.mapper;

import by.fizzly.common.dto.quiz.QuizQuestionCreateDTO;
import com.fizzly.backend.entity.QuizQuestion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizQuestionMapper {

    QuizQuestion toQuizQuestion(QuizQuestionCreateDTO createDTO);
}
