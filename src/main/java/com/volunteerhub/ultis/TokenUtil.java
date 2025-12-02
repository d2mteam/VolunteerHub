package com.volunteerhub.ultis;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenUtil {
    private static final SecureRandom RNG = new SecureRandom();

    public static String generateRawToken(int bytes) {
        byte[] buffer = new byte[bytes];
        RNG.nextBytes(buffer);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buffer);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}