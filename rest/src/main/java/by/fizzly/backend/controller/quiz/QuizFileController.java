package by.fizzly.backend.controller.quiz;

import by.fizzly.backend.service.quiz.FullQuizService;
import by.fizzly.common.dto.quiz.QuizContentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files/quiz")
@RequiredArgsConstructor
public class QuizFileController {

    private final FullQuizService fullQuizService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveQuestionContent(
            @RequestPart("quiz") QuizContentRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        fullQuizService.saveQuestionContent(request, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get-content")
    public ResponseEntity<byte[]> getQuestionContent(@RequestBody QuizContentRequest request) {
        return ResponseEntity.ok(
                fullQuizService.getQuestionContent(request.getQuestionId(), request.getQuizId())
        );
    }

}
