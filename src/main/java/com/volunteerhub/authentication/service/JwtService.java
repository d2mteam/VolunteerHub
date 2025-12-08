package com.volunteerhub.authentication.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
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
import java.util.UUID;

@Service
public class JwtService {

    @Value("${security.app.jwtSecret}")
    private String jwtSecret;

    @Value("${security.app.jwtAccessExpirationMs}")
    private int jwtAccessExpirationMs;

    @Value("${security.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    // ===================== GENERATION ===================== //

    public String generateAccessToken(UUID userId, List<String> roles) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "access_token")
                .claim("user_id", userId.toString())
                .claim("roles", roles)
                .jwtID(randomJti())
                .issueTime(new Date())
                .expirationTime(expTime(jwtAccessExpirationMs))
                .build();
        return signClaims(claims);
    }

    public String generateRefreshToken(UUID userId) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "refresh_token")
                .claim("user_id", userId.toString())
                .jwtID(randomJti())
                .issueTime(new Date())
                .expirationTime(expTime(jwtRefreshExpirationMs))
                .build();
        return signClaims(claims);
    }

    // ===================== VALIDATION / DECODE ===================== //

    public DecodedToken decodeAndValidate(String token) throws ParseException {
        JWTClaimsSet claims = parseAndValidate(token);

        return new DecodedToken(
                claims.getStringClaim("token_type"),
                UUID.fromString(claims.getStringClaim("user_id")),
                claims.getJWTID(),
                claims.getExpirationTime(),
                extractRoles(claims)
        );
    }

    private List<String> extractRoles(JWTClaimsSet claims) {
        try {
            return claims.getStringListClaim("roles");
        } catch (Exception e) {
            return null; // refresh token không có roles
        }
    }

    // ===================== PRIVATE UTILS ===================== //

    private Date expTime(long ms) {
        return new Date(System.currentTimeMillis() + ms);
    }

    private JWTClaimsSet parseAndValidate(String token) {
        JWTClaimsSet claims = parse(token);

        Date exp = claims.getExpirationTime();
        if (exp == null || exp.before(new Date())) {
            throw new JwtException("Token expired");
        }

        return claims;
    }

    private JWTClaimsSet parse(String token) {
        try {
            JWSObject jws = JWSObject.parse(token);

            boolean ok = jws.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)));
            if (!ok) {
                throw new JwtException("Signature invalid");
            }

            return JWTClaimsSet.parse(jws.getPayload().toJSONObject());

        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new JwtException("Token signing failed", e);
        }
    }

    private String randomJti() {
        return UUID.randomUUID().toString();
    }

    public record DecodedToken(
            String type,
            UUID userId,
            String jti,
            Date exp,
            List<String> roles
    ) {}
}
