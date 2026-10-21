package com.co.entrenamiento.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash de contraseñas. Version minima educativa con SHA-256 (viene con el JDK, sin libreria
 * nueva); el micro real usa bcrypt. Nunca se guarda ni se compara la clave en texto plano.
 */
public final class Claves {

    private Claves() {
    }

    public static String hash(final String clave) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] bytes = digest.digest(clave.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hex = new StringBuilder();
            for (final byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (final NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }
}
