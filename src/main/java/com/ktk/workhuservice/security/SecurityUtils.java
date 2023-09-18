package com.ktk.workhuservice.security;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class SecurityUtils {

    public static String encryptSecret(String secret) {
        String secretHash = encrypt(secret);
        return Base64.getEncoder().withoutPadding().encodeToString(secretHash.getBytes());
    }

    private static String encrypt(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-512");
            digest.update(secret.getBytes());
            return new String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An exception occurred during encryption.");
        }
    }

}
