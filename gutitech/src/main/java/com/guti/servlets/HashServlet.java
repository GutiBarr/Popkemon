package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guti.security.PasswordHasher;

/**
 * Servlet de utilidad para generar hashes Argon2id durante el desarrollo.
 * <p>
 * Genera y devuelve en texto plano el hash Argon2id de una contraseña
 * predefinida. Se usa puntualmente para obtener el hash de la contraseña
 * del usuario administrador de prueba e insertarlo manualmente en la
 * base de datos.
 * </p>
 * <p>
 * <strong>Importante:</strong> este servlet debe eliminarse o protegerse
 * antes de desplegar la aplicación en un entorno de producción, ya que
 * expone una contraseña hardcodeada.
 * </p>
 * <p>URL de acceso: {@code /hash}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.security.PasswordHasher
 */
@WebServlet(urlPatterns = {"/hash"})
public class HashServlet extends HttpServlet {

    /**
     * Genera el hash Argon2id de la contraseña de prueba y lo escribe en la respuesta.
     * <p>
     * Llama a {@link PasswordHasher#hashPassword} con la contraseña hardcodeada
     * y escribe el resultado directamente en la respuesta HTTP como texto plano.
     * El hash resultante puede copiarse y usarse en un {@code INSERT} de SQL
     * para crear el usuario administrador de prueba.
     * </p>
     *
     * @param req  petición HTTP
     * @param resp respuesta HTTP donde se escribe el hash generado
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al escribir la respuesta
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String hash = PasswordHasher.hashPassword("Pokemon123!".toCharArray());
        resp.getWriter().write(hash);
    }
}