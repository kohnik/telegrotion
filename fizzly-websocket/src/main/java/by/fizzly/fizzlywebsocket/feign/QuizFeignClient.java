package by.fizzly.fizzlywebsocket.feign;

import by.fizzly.common.dto.quiz.FullQuizGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "quiz-client", url = "${fizzly.url}")
public interface QuizFeignClient {

    @GetMapping(value = "/full-quiz/{quizId}", produces = "application/json")
    FullQuizGetDTO getFullQuizById(@PathVariable("quizId") Long quizId);

}
