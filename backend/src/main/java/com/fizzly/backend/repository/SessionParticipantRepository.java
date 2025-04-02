package com.fizzly.backend.repository;

import com.fizzly.backend.entity.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
}
