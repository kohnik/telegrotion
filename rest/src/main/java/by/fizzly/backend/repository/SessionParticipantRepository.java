package by.fizzly.backend.repository;

import by.fizzly.backend.entity.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
}
