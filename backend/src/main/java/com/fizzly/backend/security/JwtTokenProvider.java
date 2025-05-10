package com.fizzly.backend.security;

import com.fizzly.backend.entity.User;
import com.fizzly.backend.entity.UserSession;
import com.fizzly.backend.exception.FizzlyGlobalException;
import com.fizzly.backend.repository.UserSessionRepository;
import com.fizzly.backend.security.dto.AuthResponseDto;
import com.fizzly.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private final UserService userService;
    private final UserSessionRepository userSessionRepository;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("role", customUserDetails.getRole());
            claims.put("username", customUserDetails.getUsername());
        }
        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .expiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 30 seconds
                .signWith(getSigningKey()).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey));
    }

    public String generateRefreshToken(long userId, String username) {
        return Jwts.builder()
                .claim("userId", userId)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 60)) // 60 days
                .signWith(getSigningKey()).compact();
    }

    public AuthResponseDto refreshToken(String refreshTokenUUID) {
        if (refreshTokenUUID == null) {
            throw new FizzlyGlobalException("Token is null");
        }

        UUID refreshUUID = UUID.fromString(refreshTokenUUID);

        UserSession userSession = userSessionRepository.findById(refreshUUID)
                .orElseThrow(() -> new NoSuchElementException(String.format("No such token with id '%s'", refreshTokenUUID)));

        userSessionRepository.delete(userSession);

        if (!isTokenValid(userSession.getRefreshToken())) {
            throw new FizzlyGlobalException(String.format("Token expired with id '%s'", refreshTokenUUID));
        }

        User user = userService.getByUsername(userSession.getUsername());
        var jwt = generateToken(user);
        var refreshToken = generateRefreshToken(user.getId(), userSession.getUsername());
        UUID uuid = UUID.randomUUID();

        userSession = UserSession.builder()
                .refreshToken(refreshToken)
                .expiresIn(extractExpiration(refreshToken))
                .id(uuid)
                .username(user.getUsername())
                .build();
        userSessionRepository.save(userSession);


        return new AuthResponseDto(jwt, userSession.getRefreshToken(), userSession.getId());
    }
}
