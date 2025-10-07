package com.volunteerhub.authentication.config.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.volunteerhub.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthenticationJwtTokenFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = parseJwt(request);
        if (token != null && jwtService.validateToken(token)) {
            JWTClaimsSet claims;
            String username = jwtService.usernameFromToken(token);
            List<String> roles = jwtService.rolesFromAccessToken(token);

            List<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
