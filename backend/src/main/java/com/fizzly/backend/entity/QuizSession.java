package com.fizzly.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class QuizSession implements Serializable {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "join_code")
    private String joinCode;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToMany
    private List<SessionParticipant> participants = new ArrayList<>();
}
