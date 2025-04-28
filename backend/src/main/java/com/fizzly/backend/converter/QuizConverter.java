package com.fizzly.backend.converter;

import com.fizzly.backend.dto.quiz.FullQuizAnswerGetDTO;
import com.fizzly.backend.dto.quiz.FullQuizQuestionGetDTO;
import com.fizzly.backend.dto.quiz.QuizSessionAnswerDTO;
import com.fizzly.backend.dto.quiz.QuizSessionDTO;

public class QuizConverter {

    public static QuizSessionDTO convertToQuizSessionDTO(FullQuizQuestionGetDTO quizDTO) {
        QuizSessionDTO quizSessionDTO = new QuizSessionDTO();
        quizSessionDTO.setQuestionId(quizDTO.getQuestionId());
        quizSessionDTO.setQuestionName(quizDTO.getQuestion());
        quizSessionDTO.setTimeLeft(quizDTO.getSeconds());
        quizSessionDTO.setPoints(quizDTO.getPoints());
        quizSessionDTO.setAnswers(quizDTO.getAnswers().stream()
                .map(QuizConverter::convertToQuizSessionAnswerDTO)
                .toList());
        return quizSessionDTO;
    }

    private static QuizSessionAnswerDTO convertToQuizSessionAnswerDTO(FullQuizAnswerGetDTO answerDTO) {
        QuizSessionAnswerDTO quizSessionAnswerDTO = new QuizSessionAnswerDTO();
        quizSessionAnswerDTO.setAnswer(answerDTO.getAnswer());
        quizSessionAnswerDTO.setOrder(answerDTO.getOrder());
        quizSessionAnswerDTO.setCorrect(answerDTO.isCorrect());
        return quizSessionAnswerDTO;
    }

}
