package by.fizzly.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "expires_in", nullable = false)
    private Date expiresIn;

    @Column(name = "username", nullable = false)
    private String username;

}
