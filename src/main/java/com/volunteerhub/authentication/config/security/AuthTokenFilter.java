package com.volunteerhub.authentication.config.security;

import com.volunteerhub.authentication.config.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = parseJwt(request);
//        if (token != null && jwtUtils.validateToken(token)) {
//            JWTClaimsSet claims;
//            String username;
//            List<String> roles;
//
//            try {
//                claims = jwtUtils.getClaims(token);
//                username = claims.getStringClaim("username");
//                roles =  claims.getStringListClaim("roles");
//            } catch (ParseException | JOSEException e) {
//                throw new RuntimeException(e);
//            }
//
//            List<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }

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
