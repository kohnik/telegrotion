package com.fizzly.backend.security;

import com.fizzly.backend.security.dto.JwtResponseDto;
import com.fizzly.backend.security.dto.SignInRequest;
import com.fizzly.backend.security.dto.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String ROOT_PATH = "/";
    private static final int REFRESH_COOKIE_AGE = 60 * 60 * 24 * 60; // 60 days

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtService;

    @Operation(summary = "User registration")
    @PostMapping("/sign-up")
    public ResponseEntity<JwtResponseDto> signUp(@RequestBody @Valid SignUpRequest request) {
        var authResponseDto = authenticationService.signUp(request);

        return ResponseEntity.status(201).body(new JwtResponseDto(authResponseDto.getAccessToken()));
    }

    @Operation(summary = "User authorization")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponseDto> signIn(@RequestBody @Valid SignInRequest signInRequest, HttpServletResponse response) {
        var authResponseDto = authenticationService.signIn(signInRequest);

        Cookie cookie = createRefreshTokenCookie(authResponseDto.getRefreshUUID());
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponseDto(authResponseDto.getAccessToken()));
    }

    @Operation(summary = "Refresh tokens")
    @PostMapping("/refresh-tokens")
    public ResponseEntity<JwtResponseDto> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshTokenUUID,
                                                       HttpServletResponse response) {
        var authResponseDto = jwtService.refreshToken(refreshTokenUUID);

        Cookie cookie = createRefreshTokenCookie(authResponseDto.getRefreshUUID());
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponseDto(authResponseDto.getAccessToken()));
    }

    @Operation(summary = "Validate token")
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        return ResponseEntity.ok(jwtService.isTokenValid(token));
    }

    private Cookie createRefreshTokenCookie(UUID uuid) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, uuid.toString());
        cookie.setHttpOnly(true);
        cookie.setPath(ROOT_PATH);
        cookie.setMaxAge(REFRESH_COOKIE_AGE);
        return cookie;
    }

}

