package com.fizzly.backend.service.quiz;

import com.fizzly.backend.dto.FullQuizAnswerGetDTO;
import com.fizzly.backend.dto.FullQuizCreateDTO;
import com.fizzly.backend.dto.FullQuizGetDTO;
import com.fizzly.backend.dto.FullQuizQuestionGetDTO;
import com.fizzly.backend.dto.GetListQuizDTO;
import com.fizzly.backend.dto.UserGetDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.entity.QuizQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
            quizQuestion.setQuestion(questionDTO.getQuestion());
            quizQuestion.setPoints(questionDTO.getPoints());
            quizQuestion.setOrdering(questionDTO.getOrder());
            quizQuestion.setSeconds(questionDTO.getSeconds());

            QuizQuestion createdQuestion = quizQuestionService.addQuestionToQuiz(quizQuestion, createdQuiz.getId());

            questionDTO.getAnswers().forEach(answerDTO -> {
                QuizAnswer quizAnswer = new QuizAnswer();
                quizAnswer.setAnswer(answerDTO.getAnswer());
                quizAnswer.setCorrect(answerDTO.isCorrect());
                quizAnswer.setOrdering(answerDTO.getOrder());

                quizAnswerService.addQuizAnswerToQuiz(quizAnswer, createdQuestion.getId());
            });
        });

        return quiz;
    }

    public FullQuizGetDTO getFullQuiz(Long quizId) {
        FullQuizGetDTO fullQuizGetDTO = new FullQuizGetDTO();

        Quiz quiz = quizService.findByQuizId(quizId);
        fullQuizGetDTO.setName(quiz.getName());

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setUsername(quiz.getOwner() != null ? quiz.getOwner().getUsername() : null);
        fullQuizGetDTO.setUser(userGetDTO);

        List<QuizQuestion> quizzes = quizQuestionService.findAllByQuizId(quizId);
        List<FullQuizQuestionGetDTO> questionsDTO = new ArrayList<>();
        quizzes.forEach(quizQuestion -> {
            FullQuizQuestionGetDTO quizQuestionGetDTO = new FullQuizQuestionGetDTO();
            quizQuestionGetDTO.setQuestionId(quizQuestion.getId());
            quizQuestionGetDTO.setQuestion(quizQuestion.getQuestion());
            quizQuestionGetDTO.setPoints(quizQuestion.getPoints());
            quizQuestionGetDTO.setOrder(quizQuestion.getOrdering());
            quizQuestionGetDTO.setSeconds(quizQuestion.getSeconds());

            List<QuizAnswer> answers = quizAnswerService.getAllAnswersByQuestionId(quizQuestion.getId());
            List<FullQuizAnswerGetDTO> fullQuizAnswerGetDTOS = new ArrayList<>();
            answers.forEach(quizAnswer -> {
                FullQuizAnswerGetDTO quizAnswerGetDTO = new FullQuizAnswerGetDTO();
                quizAnswerGetDTO.setAnswer(quizAnswer.getAnswer());
                quizAnswerGetDTO.setCorrect(quizAnswer.isCorrect());
                quizAnswerGetDTO.setOrder(quizAnswer.getOrdering());

                fullQuizAnswerGetDTOS.add(quizAnswerGetDTO);
            });
            quizQuestionGetDTO.setAnswers(fullQuizAnswerGetDTOS);

            questionsDTO.add(quizQuestionGetDTO);
        });
        fullQuizGetDTO.setQuestions(questionsDTO);

        return fullQuizGetDTO;
    }

    @Transactional
    public List<GetListQuizDTO> getAllQuizzesByUserId(Long userId) {
        List<Quiz> quizzes = quizService.findAllQuizzesByUser(userId);
        return quizzes.stream()
                .map(quiz -> {
                    GetListQuizDTO dto = new GetListQuizDTO();
                    dto.setName(quiz.getName());
                    dto.setQuizId(quiz.getId());
                    dto.setUsername(quiz.getOwner().getUsername());
                    dto.setQuestionCount(quizQuestionService.findAllByQuizId(quiz.getId()).size());

                    return dto;
                })
                .toList();
    }

    @Transactional
    public void deleteQuizById(Long quizId) {
        Quiz quiz = quizService.findByQuizId(quizId);
        List<QuizQuestion> questions = quizQuestionService.findAllByQuizId(quizId);
        questions.forEach(question -> {
            List<QuizAnswer> answers = quizAnswerService.getAllAnswersByQuestionId(question.getId());
            quizAnswerService.deleteAnswers(answers);
        });
        quizQuestionService.deleteQuestions(questions);
        quizService.deleteQuiz(quiz);
    }
}
