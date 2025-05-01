package com.fizzly.backend.mapper;

import by.fizzly.common.dto.quiz.QuizCreateDTO;
import com.fizzly.backend.entity.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);
}
