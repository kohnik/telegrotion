package com.fizzly.backend.service;

import com.fizzly.backend.dto.FullQuizCreateDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.entity.QuizQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FullQuizService {

    private final QuizService quizService;
    private final QuizAnswerService quizAnswerService;
    private final QuizQuestionService quizQuestionService;

    @Transactional
    public Quiz createFullQuiz(FullQuizCreateDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setName(quizDTO.getName());

        Quiz createdQuiz = quizService.createQuiz(quiz, quizDTO.getUserId());
        quizDTO.getQuestions().forEach(questionDTO -> {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuiz(createdQuiz);
            quizQuestion.setPoints(questionDTO.getPoints());

            QuizQuestion createdQuestion = quizQuestionService.addQuestionToQuiz(quizQuestion, createdQuiz.getId());

            questionDTO.getAnswers().forEach(answerDTO -> {
                QuizAnswer quizAnswer = new QuizAnswer();
                quizAnswer.setAnswer(answerDTO.getAnswer());
                quizAnswer.setCorrect(answerDTO.isCorrect());

                quizAnswerService.addQuizAnswerToQuiz(quizAnswer, createdQuestion.getId());
            });
        });

        return quiz;
    }

}
