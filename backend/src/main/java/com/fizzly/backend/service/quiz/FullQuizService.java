package com.fizzly.backend.service.quiz;

import by.fizzly.common.dto.UserGetDTO;
import by.fizzly.common.dto.quiz.FullQuizAnswerGetDTO;
import by.fizzly.common.dto.quiz.FullQuizCreateDTO;
import by.fizzly.common.dto.quiz.FullQuizGetDTO;
import by.fizzly.common.dto.quiz.FullQuizQuestionGetDTO;
import by.fizzly.common.dto.quiz.GetListQuizDTO;
import by.fizzly.common.dto.quiz.QuizContentRequest;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.entity.QuizMediaType;
import com.fizzly.backend.entity.QuizQuestion;
import com.fizzly.backend.exception.FizzlyGlobalException;
import com.fizzly.backend.repository.QuizAnswerRepository;
import com.fizzly.backend.repository.QuizQuestionRepository;
import com.fizzly.backend.repository.QuizRepository;
import com.fizzly.backend.service.StorageService;
import com.google.cloud.storage.Blob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FullQuizService {

    private final QuizService quizService;
    private final QuizAnswerService quizAnswerService;
    private final QuizQuestionService quizQuestionService;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizRepository quizRepository;
    private final StorageService storageService;

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
                quizAnswerGetDTO.setId(quizAnswer.getId());
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

    @Transactional
    public void updateQuizById(Long quizId, FullQuizGetDTO dto) {
        Quiz quiz = quizService.findByQuizId(quizId);
        updateQuiz(quiz, dto);

        List<QuizQuestion> questions = quizQuestionService.findAllByQuizId(quizId);
        updateQuestions(questions, dto.getQuestions(), quiz);

//        dto.getQuestions().forEach(questionDto -> {
//            QuizQuestion quizQuestion = quizQuestionRepository.findById(questionDto.getQuestionId())
//                    .orElseThrow(() -> new EntityNotFoundException());
//            List<QuizAnswer> answers = quizAnswerService.getAllAnswersByQuestionId(quizQuestion.getId());
//            updateAnswers(answers, questionDto.getAnswers(), quizQuestion);
//        });
    }

    private void updateQuiz(Quiz quiz, FullQuizGetDTO dto) {
        quiz.setName(dto.getName());

        quizRepository.save(quiz);
    }

    private void updateQuestions(List<QuizQuestion> questions, List<FullQuizQuestionGetDTO> dtos, Quiz quiz) {
        Map<Long, QuizQuestion> existingQuestionMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));

        Map<Long, FullQuizQuestionGetDTO> updatedDtoMap = dtos.stream()
                .collect(Collectors.toMap(FullQuizQuestionGetDTO::getQuestionId, Function.identity()));

        Iterator<QuizQuestion> iterator = questions.iterator();
        while (iterator.hasNext()) {
            QuizQuestion existingQuestion = iterator.next();
            FullQuizQuestionGetDTO updatedDto = updatedDtoMap.get(existingQuestion.getId());

            if (updatedDto == null) {
                iterator.remove();
                quizAnswerRepository.deleteAll(
                        quizAnswerService.getAllAnswersByQuestionId(existingQuestion.getId())
                );
                quizQuestionRepository.delete(existingQuestion);
            } else {
                updateQuestionFromDto(existingQuestion, updatedDto);
            }
        }

        for (FullQuizQuestionGetDTO dto : dtos) {
            if (!existingQuestionMap.containsKey(dto.getQuestionId())) {
                QuizQuestion newQuestion = createQuestionFromDto(dto, quiz);
                questions.add(newQuestion);
            }
        }
    }

    private void updateQuestionFromDto(QuizQuestion question, FullQuizQuestionGetDTO dto) {
        question.setSeconds(dto.getSeconds());
        question.setQuestion(dto.getQuestion());
        question.setPoints(dto.getPoints());
        question.setOrdering(dto.getOrder());

        updateAnswers(
                quizAnswerService.getAllAnswersByQuestionId(question.getId()),
                dto.getAnswers(),
                question
        );
        quizQuestionRepository.save(question);
    }

    private QuizQuestion createQuestionFromDto(FullQuizQuestionGetDTO dto, Quiz quiz) {
        QuizQuestion question = new QuizQuestion();

        question.setQuiz(quiz);
        question.setSeconds(dto.getSeconds());
        question.setQuestion(dto.getQuestion());
        question.setPoints(dto.getPoints());
        question.setOrdering(dto.getOrder());

        QuizQuestion savedQuestion = quizQuestionRepository.save(question);

        List<QuizAnswer> answers = dto.getAnswers().stream()
                .map(answerDto -> createAnswerFromDto(answerDto, savedQuestion))
                .toList();
        quizAnswerService.addMultipleAnswersToQuiz(answers, savedQuestion.getId());

        return savedQuestion;
    }

    public void updateAnswers(List<QuizAnswer> answers, List<FullQuizAnswerGetDTO> dtos, QuizQuestion quizQuestion) {
        Map<Long, QuizAnswer> existingQuestionMap = answers.stream()
                .collect(Collectors.toMap(QuizAnswer::getId, Function.identity()));

        Map<Long, FullQuizAnswerGetDTO> updatedDtoMap = dtos.stream()
                .collect(Collectors.toMap(FullQuizAnswerGetDTO::getId, Function.identity()));

        Iterator<QuizAnswer> iterator = answers.iterator();
        while (iterator.hasNext()) {
            QuizAnswer existingAnswer = iterator.next();
            FullQuizAnswerGetDTO updatedDto = updatedDtoMap.get(existingAnswer.getId());

            if (updatedDto == null) {
                iterator.remove();
                quizAnswerRepository.delete(existingAnswer);
            } else {
                updateAnswerFromDto(existingAnswer, updatedDto);
            }
        }

        for (FullQuizAnswerGetDTO dto : dtos) {
            if (!existingQuestionMap.containsKey(dto.getId())) {
                QuizAnswer newAnswer = createAnswerFromDto(dto, quizQuestion);
                quizAnswerRepository.save(newAnswer);
            }
        }
    }

    private void updateAnswerFromDto(QuizAnswer answer, FullQuizAnswerGetDTO dto) {
        answer.setCorrect(dto.isCorrect());
        answer.setAnswer(dto.getAnswer());
        answer.setOrdering(dto.getOrder());

        quizAnswerRepository.save(answer);
    }

    private QuizAnswer createAnswerFromDto(FullQuizAnswerGetDTO dto, QuizQuestion question) {
        QuizAnswer answer = new QuizAnswer();

        answer.setCorrect(dto.isCorrect());
        answer.setAnswer(dto.getAnswer());
        answer.setOrdering(dto.getOrder());
        answer.setQuestion(question);

        return answer;
    }

    @Transactional
    public void saveQuestionContent(QuizContentRequest request, MultipartFile file) {
        Quiz quiz = quizService.findByQuizId(request.getQuizId());
        QuizQuestion question = quizQuestionService.findById(request.getQuestionId());

        String fileName = UUID.randomUUID().toString();
        QuizMediaType quizMediaType = QuizMediaType.fromContentType(file.getContentType());

        question.setMediaType(quizMediaType);
        question.setFilePath(fileName);
        quizQuestionRepository.save(question);

        try {
            storageService.uploadFile(file.getInputStream(), fileName, String.valueOf(quiz.getId()));
        } catch (IOException e) {
            throw new FizzlyGlobalException(e.getMessage(), e);
        }
    }

    public byte[] getQuestionContent(Long questionId, Long quizId) {
        Quiz quiz = quizService.findByQuizId(quizId);
        QuizQuestion question = quizQuestionService.findById(questionId);

        Blob blob = storageService.getFile(question.getFilePath(), String.valueOf(quiz.getId()));

        return blob == null ? null : blob.getContent();
    }
}
