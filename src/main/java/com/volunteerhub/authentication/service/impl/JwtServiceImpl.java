package com.volunteerhub.authentication.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.volunteerhub.authentication.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
    public String generateAccessToken(String username, List<String> roles) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "access_token")
                .claim("username", username)
                .claim("roles", roles)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
                .build();

        return getString(claims);
    }

    @Override
    public String generateRefreshToken(String username) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_type", "refresh_token")
                .claim("username", username)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .build();

        return getString(claims);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            if (claims == null) {
                logger.warn("JWT token verification failed");
                return false;
            }

            if (claims.getExpirationTime().before(new Date())) {
                logger.warn("JWT token has expired: {}", claims.getExpirationTime());
                return false;
            }
            return true;
        } catch (ParseException e) {
            logger.error("JWT token parse error: {}", e.getMessage());
        } catch (JOSEException e) {
            logger.error("JWT token signature verification failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while validating JWT token: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String usernameFromToken(String token) {
        try {
            JWTClaimsSet claimsSet = getClaims(token);
            if (claimsSet == null) {
                return null;
            }
            return  claimsSet.getClaim("username").toString();
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    @Override
    public List<String> rolesFromToken(String token) {
        try {
            JWTClaimsSet claimsSet = getClaims(token);
            if (claimsSet == null) {
                return null;
            }
            return  claimsSet.getStringListClaim("roles");
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    private JWTClaimsSet getClaims(String token) throws ParseException, JOSEException {
        JWSObject jws = JWSObject.parse(token);

        boolean verified = jws.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        if (!verified) {
            logger.warn("JWT token signature invalid");
            return null;
        }

        return JWTClaimsSet.parse(jws.getPayload().toJSONObject());
    }


    private String getString(JWTClaimsSet claims) {
        JWSObject jws = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claims.toJSONObject()));

        try {
            jws.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jws.serialize();
    }
}
