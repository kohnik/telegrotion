package com.fizzly.backend.mapper;

import com.fizzly.backend.dto.quiz.QuizCreateDTO;
import com.fizzly.backend.entity.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);
}
