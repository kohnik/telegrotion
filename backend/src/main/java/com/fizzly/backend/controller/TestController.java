package com.fizzly.backend.controller;

import com.fizzly.backend.service.StorageService;
import com.google.cloud.storage.Blob;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final StorageService storageService;

    @PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void test(
            @RequestPart("test") Test test,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        System.out.println("test.name = " + test.getName());
        System.out.println("file.getContentType() = " + file.getContentType());

        storageService.uploadFile(file.getInputStream(), file.getOriginalFilename(), "test");
    }

    @GetMapping("/test2")
    public byte[] test2(@RequestBody Test test) {
        Blob blob = storageService.getFile(test.getName(), "test");
        return blob.getContent();
    }

    @Getter
    @Setter
    public static class Test {
        private String name;
    }
}
