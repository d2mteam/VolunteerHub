package com.volunteerhub.configuration.security;

import com.volunteerhub.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractJwt(request);

        if (token != null && jwtService.validateToken(token)) {
            authenticate(token);
        } else {
            authenticateAnonymous();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        UUID userId = jwtService.getUserIdFromToken(token).orElse(null);
        List<String> roles = jwtService.rolesFromToken(token).orElse(null);

        if (userId == null || roles == null) {
            return;
        }

        List<SimpleGrantedAuthority> authorities =
                roles.stream().map(SimpleGrantedAuthority::new).toList();

        Authentication auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void authenticateAnonymous() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ANONYMOUS"));
        Authentication auth = new UsernamePasswordAuthenticationToken("ANONYMOUS", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
