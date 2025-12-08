package com.volunteerhub.authentication.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.volunteerhub.ultis.exception.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class JwtService {
    @Value("${security.app.jwtSecret}")
    private String jwtSecret;

    @Value("${security.app.jwtAccessExpirationMs}")
    private int jwtAccessExpirationMs;

    @Value("${security.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    // PUBLIC

    public String generateAccessToken(UUID userId, List<String> roles) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "access_token")
                .jwtID(randomJti())
                .claim("user_id", userId.toString())
                .claim("roles", roles)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
                .build();
        return signClaims(claims);
    }

    public String generateAccessToken(UUID userId) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "access_token")
                .jwtID(randomJti())
                .claim("user_id", userId.toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
                .build();
        return signClaims(claims);
    }

    public String generateRefreshToken(UUID userId) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "refresh_token")
                .jwtID(randomJti())
                .claim("user_id", userId.toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .build();
        return signClaims(claims);
    }

    public boolean validateToken(String token) {
        try {
            parseAndValidate(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Optional<List<String>> rolesFromToken(String token) {
        try {
            JWTClaimsSet claims = parseAndValidate(token);
            return Optional.ofNullable(claims.getStringListClaim("roles"));
        } catch (JwtException | ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getTokenType(String token) {
        try {
            JWTClaimsSet claims = parseAndValidate(token);
            return Optional.ofNullable(claims.getStringClaim("token_type"));
        } catch (JwtException | ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<UUID> getUserIdFromToken(String token) {
        try {
            JWTClaimsSet claims = parseAndValidate(token);
            String id = claims.getStringClaim("user_id");
            return Optional.of(UUID.fromString(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getJti(String token) {
        try {
            JWTClaimsSet claims = parseAndValidate(token);
            return Optional.ofNullable(claims.getJWTID());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Date> getExpiration(String token) {
        try {
            JWTClaimsSet claims = parseAndValidate(token);
            return Optional.ofNullable(claims.getExpirationTime());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // PRIVATE UTILS

    private JWTClaimsSet parseAndValidate(String token) {
        JWTClaimsSet claims = parse(token);

        if (claims.getExpirationTime().before(new Date())) {
            throw new JwtException("Token expired");
        }

        return claims;
    }

    private JWTClaimsSet parse(String token) {
        try {
            JWSObject jws = JWSObject.parse(token);
            boolean verified = jws.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)));

            if (!verified) {
                throw new JwtException("Signature invalid");
            }

            return JWTClaimsSet.parse(jws.getPayload().toJSONObject());
        } catch (ParseException | JOSEException e) {
            throw new JwtException("Token parse/verify failed", e);
        }
    }

    private String signClaims(JWTClaimsSet claims) {
        try {
            JWSObject jws = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS256),
                    new Payload(claims.toJSONObject())
            );
            jws.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
            return jws.serialize();
        } catch (JOSEException e) {
            throw new JwtException("Token signing failed", e);
        }
    }

    private String randomJti() {
        return new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong()).toString();
    }
}
