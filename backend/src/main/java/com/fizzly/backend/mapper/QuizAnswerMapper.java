package com.fizzly.backend.mapper;

import com.fizzly.backend.dto.QuizAnswerCreateDTO;
import com.fizzly.backend.entity.QuizAnswer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizAnswerMapper {

    QuizAnswer toQuizAnswer(QuizAnswerCreateDTO createDTO);

    List<QuizAnswer> toQuizAnswerList(List<QuizAnswerCreateDTO> answers);
}
