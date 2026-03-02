package com.guti.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordHasher {
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 1;

    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String hashPassword(char[] password) {
        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password);
        } finally {
            argon2.wipeArray(password);
        }
    }

    public static boolean verifyPassword(String hash, char[] password) {
        try {
            return argon2.verify(hash, password);
        } finally {
            argon2.wipeArray(password);
        }
    }
}
