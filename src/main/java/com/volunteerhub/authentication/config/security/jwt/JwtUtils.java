package com.volunteerhub.authentication.config.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.app.jwtSecret}")
    private String jwtSecret;

    @Value("${security.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateToken(String username, List<String> roles) throws JOSEException {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("username", username)
                .claim("roles", roles)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .build();

        JWSObject jws = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claims.toJSONObject())
        );

        jws.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        return jws.serialize();
    }

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

    public JWTClaimsSet getClaims(String token) throws ParseException, JOSEException {
        JWSObject jws = JWSObject.parse(token);

        boolean verified = jws.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        if (!verified) {
            logger.warn("JWT token signature invalid");
            return null;
        }

        return JWTClaimsSet.parse(jws.getPayload().toJSONObject());
    }
}
