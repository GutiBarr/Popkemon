package com.guti.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Utilidad para el hashing y verificación de contraseñas con el algoritmo Argon2id.
 * <p>
 * Argon2id es el algoritmo recomendado por OWASP para almacenamiento seguro
 * de contraseñas. Combina resistencia a ataques de canal lateral (variante Argon2i)
 * y a ataques de fuerza bruta con GPU (variante Argon2d).
 * </p>
 * <p>
 * Los parámetros configurados son:
 * </p>
 * <ul>
 *   <li><strong>Iteraciones:</strong> {@value #ITERATIONS} — número de pasadas sobre la memoria.</li>
 *   <li><strong>Memoria:</strong> {@value #MEMORY} KB — memoria RAM utilizada por el algoritmo.</li>
 *   <li><strong>Paralelismo:</strong> {@value #PARALLELISM} — número de hilos paralelos.</li>
 * </ul>
 * <p>
 * Los métodos reciben la contraseña como {@code char[]} en lugar de {@code String}
 * para poder limpiarla de memoria con {@code wipeArray} en el bloque {@code finally},
 * evitando que quede expuesta en el heap de la JVM.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
public class PasswordHasher {

    /** Número de iteraciones (pasadas) del algoritmo Argon2id. */
    private static final int ITERATIONS = 10;

    /** Memoria en KB utilizada por el algoritmo Argon2id. */
    private static final int MEMORY = 65536;

    /** Número de hilos paralelos utilizados por el algoritmo Argon2id. */
    private static final int PARALLELISM = 1;

    /** Instancia de Argon2id reutilizada en todos los hashes y verificaciones. */
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Genera un hash Argon2id de una contraseña.
     * <p>
     * El hash resultante incluye la sal y todos los parámetros necesarios
     * para la verificación posterior, por lo que es autocontenido y puede
     * almacenarse directamente en la base de datos.
     * La contraseña se borra de memoria en el bloque {@code finally}
     * mediante {@code wipeArray} para minimizar el tiempo de exposición.
     * </p>
     *
     * @param password contraseña en texto plano como array de caracteres
     * @return hash Argon2id listo para almacenar en la base de datos
     */
    public static String hashPassword(char[] password) {
        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password);
        } finally {
            argon2.wipeArray(password);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash Argon2id.
     * <p>
     * Extrae automáticamente la sal y los parámetros del hash almacenado
     * para realizar la comparación de forma segura. La contraseña se borra
     * de memoria en el bloque {@code finally} tras la verificación.
     * </p>
     *
     * @param hash     hash Argon2id almacenado en la base de datos
     * @param password contraseña introducida por el usuario como array de caracteres
     * @return {@code true} si la contraseña es correcta, {@code false} en caso contrario
     */
    public static boolean verifyPassword(String hash, char[] password) {
        try {
            return argon2.verify(hash, password);
        } finally {
            argon2.wipeArray(password);
        }
    }
}
