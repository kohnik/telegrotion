package com.fizzly.backend.entity;

import com.fizzly.backend.exception.TelegrotionException;

public enum QuizMediaType {

    VIDEO,
    AUDIO,
    IMAGE;


    public static QuizMediaType fromContentType(String contentType) {
        if (contentType.startsWith("image")) {
            return QuizMediaType.IMAGE;
        }
        if (contentType.startsWith("video")) {
            return QuizMediaType.VIDEO;
        }
        if (contentType.startsWith("audio")) {
            return QuizMediaType.AUDIO;
        }
        throw new TelegrotionException("Данный тип файла не поддерживается: " + contentType);
    }

}
