package com.fizzly.backend.service;

import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.User;
import com.fizzly.backend.exception.QuizNotFoundException;
import com.fizzly.backend.exception.UserNotFoundException;
import com.fizzly.backend.repository.QuizRepository;
import com.fizzly.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public Quiz createQuiz(Quiz quiz, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        quiz.setOwner(user);

        return quizRepository.save(quiz);
    }

    public List<Quiz> findAllQuizzesByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return quizRepository.findAllByOwner(user);
    }

    public Quiz findByQuizId(Long quizId) {
        return quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));
    }
}
