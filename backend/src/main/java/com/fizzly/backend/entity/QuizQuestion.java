package com.fizzly.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "QUIZ_QUESTIONS")
@Getter
@Setter
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @ManyToOne
    @JsonIgnore
    private Quiz quiz;

    private int points;

    private int ordering;

    private int seconds;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "media_type")
    private QuizMediaType mediaType;

    @Column(name = "file_path")
    private String filePath;

}
