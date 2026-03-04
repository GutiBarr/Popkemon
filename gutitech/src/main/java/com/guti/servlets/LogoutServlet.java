package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet que gestiona el cierre de sesión del usuario.
 * <p>
 * Invalida la sesión HTTP activa, eliminando todos los atributos almacenados
 * (usuario, mensajes, etc.) y redirige al catálogo de la tienda.
 * Usa {@code getSession(false)} para no crear una sesión nueva si el usuario
 * ya no tiene ninguna activa.
 * </p>
 * <p>URL de acceso: {@code /logout}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    /**
     * Invalida la sesión activa del usuario y redirige a la tienda.
     * <p>
     * Si existe una sesión activa, la invalida con {@code sesion.invalidate()},
     * lo que elimina todos sus atributos incluyendo el {@code UsuarioSessionDTO}.
     * Si no hay sesión, no hace nada y redirige igualmente a {@code /tienda}.
     * </p>
     *
     * @param req  petición HTTP del usuario que quiere cerrar sesión
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al redirigir la respuesta
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }

        resp.sendRedirect("tienda");
    }
}