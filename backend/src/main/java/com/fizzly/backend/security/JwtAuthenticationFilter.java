package com.fizzly.backend.security;

import com.fizzly.backend.security.dto.AuthResponseDto;
import com.fizzly.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final JwtTokenProvider jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authHeader.substring(BEARER_PREFIX.length());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                boolean tokenValid = jwtService.isTokenValid(jwt);
                if (tokenValid) {
                    var username = jwtService.extractUserName(jwt);
                    UserDetails userDetails = userService
                            .userDetailsService()
                            .loadUserByUsername(username);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            } catch (ExpiredJwtException e) {
                Cookie[] cookies = request.getCookies();
                boolean containsRefreshToken = false;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        containsRefreshToken = true;
                        String refreshTokenUUID = cookie.getValue();

                        AuthResponseDto authResponseDto = jwtService.refreshToken(refreshTokenUUID);

                        Cookie cookieNew = new Cookie("refreshToken", authResponseDto.getRefreshUUID().toString());
                        cookieNew.setHttpOnly(true);
                        cookieNew.setPath("/");
                        cookieNew.setMaxAge(60 * 60 * 24 * 60);

                        response.addCookie(cookieNew);
                        response.setHeader("Authorization", "Bearer " + authResponseDto.getAccessToken());

                        doAuth(request, authResponseDto.getAccessToken());
                        break;
                    }
                }
                if (!containsRefreshToken) {
                    throw e;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void doAuth(HttpServletRequest request, String jwt) {
        var username = jwtService.extractUserName(jwt);
        UserDetails userDetails = userService
                .userDetailsService()
                .loadUserByUsername(username);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

}
