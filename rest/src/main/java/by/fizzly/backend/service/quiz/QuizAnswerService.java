package by.fizzly.backend.service.quiz;

import by.fizzly.backend.exception.QuizQuestionNotFoundException;
import by.fizzly.backend.entity.QuizAnswer;
import by.fizzly.backend.entity.QuizQuestion;
import by.fizzly.backend.repository.QuizAnswerRepository;
import by.fizzly.backend.repository.QuizQuestionRepository;
import by.fizzly.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAnswerService {

    private final QuizAnswerRepository answerRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuizAnswer addQuizAnswerToQuiz(QuizAnswer quizAnswer, Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        quizAnswer.setQuestion(question);

        return answerRepository.save(quizAnswer);
    }

    public List<QuizAnswer> addMultipleAnswersToQuiz(List<QuizAnswer> answers, Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        answers.forEach(answer -> answer.setQuestion(question));

        return answerRepository.saveAll(answers);
    }

    public List<QuizAnswer> getAllAnswersByQuestionId(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        return answerRepository.findAllByQuestion(question);
    }

    @Transactional
    public void deleteAnswers(List<QuizAnswer> answers) {
        answerRepository.deleteAll(answers);
    }
}
