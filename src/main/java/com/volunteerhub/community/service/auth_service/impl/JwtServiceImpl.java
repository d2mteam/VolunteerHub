package com.volunteerhub.community.service.auth_service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.volunteerhub.community.service.auth_service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${security.app.jwtSecret}")
    private String jwtSecret;

    @Value("${security.app.jwtAccessExpirationMs}")
    private int jwtAccessExpirationMs;

    @Value("${security.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Override
    public String generateAccessToken(UUID userId, List<String> roles) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "access_token")
                .claim("user_id", userId.toString())
                .claim("roles", roles)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
                .build();

        return signClaims(claims);
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "refresh_token")
                .claim("user_id", userId.toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .build();

        return signClaims(claims);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            if (claims == null) {
                logger.warn("Invalid JWT signature");
                return false;
            }

            if (claims.getExpirationTime().before(new Date())) {
                logger.warn("JWT token expired at {}", claims.getExpirationTime());
                return false;
            }
            return true;
        } catch (ParseException | JOSEException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> rolesFromToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            if (claims == null) return null;
            return claims.getStringListClaim("roles");
        } catch (ParseException | JOSEException e) {
            logger.error("Failed to parse roles from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            if (claims == null) return null;
            String id = claims.getStringClaim("user_id");
            return UUID.fromString(id);
        } catch (ParseException | JOSEException | IllegalArgumentException e) {
            logger.error("Failed to extract user_id from token: {}", e.getMessage());
            return null;
        }
    }

    // ===== Helper methods =====

    private JWTClaimsSet getClaims(String token) throws ParseException, JOSEException {
        JWSObject jws = JWSObject.parse(token);
        boolean verified = jws.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)));

        if (!verified) {
            logger.warn("JWT signature verification failed");
            return null;
        }

        return JWTClaimsSet.parse(jws.getPayload().toJSONObject());
    }

    private String signClaims(JWTClaimsSet claims) {
        JWSObject jws = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claims.toJSONObject())
        );

        try {
            jws.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JWT", e);
        }

        return jws.serialize();
    }
}
