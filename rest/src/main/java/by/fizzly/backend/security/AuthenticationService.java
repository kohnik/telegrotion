package by.fizzly.backend.security;

import by.fizzly.backend.security.dto.SignInRequest;
import by.fizzly.backend.entity.User;
import by.fizzly.backend.entity.UserSession;
import by.fizzly.backend.repository.UserSessionRepository;
import by.fizzly.backend.security.dto.AuthResponseDto;
import by.fizzly.backend.security.dto.SignUpRequest;
import by.fizzly.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtTokenProvider jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserSessionRepository userSessionRepository;

    public AuthResponseDto signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);

        return new AuthResponseDto(jwt, null, null);
    }

    public AuthResponseDto signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        Long userId = ((User) user).getId();

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(userId, user.getUsername());
        UUID uuid = UUID.randomUUID();

        UserSession userSession = UserSession.builder()
                .refreshToken(refreshToken)
                .expiresIn(jwtService.extractExpiration(refreshToken))
                .id(uuid)
                .username(user.getUsername())
                .build();
        userSessionRepository.save(userSession);

        return new AuthResponseDto(jwt, refreshToken, uuid);
    }

}
