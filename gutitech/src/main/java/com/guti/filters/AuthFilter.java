package com.guti.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filtro de autenticación que protege las rutas que requieren login.
 * <p>
 * Se aplica a las URLs {@code /perfil}, {@code /pedidos}, {@code /editarPerfil},
 * {@code /cambiarPassword} y {@code /finalizarPedido}. Antes de que la petición
 * llegue al servlet correspondiente, comprueba si existe el atributo
 * {@code usuario} en la sesión HTTP.
 * </p>
 * <p>
 * Si el usuario no está autenticado, guarda un mensaje informativo en la sesión
 * y redirige al formulario de login. Si está autenticado, deja pasar
 * la petición a través de la cadena de filtros.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.dto.UsuarioSessionDTO
 */
@WebFilter(urlPatterns = {"/perfil", "/pedidos", "/editarPerfil", "/cambiarPassword", "/finalizarPedido"})
public class AuthFilter implements Filter {

    /**
     * Comprueba si el usuario tiene sesión activa antes de permitir el acceso.
     * <p>
     * Usa {@code getSession(false)} para no crear una sesión nueva si no existe.
     * Si la sesión existe y contiene el atributo {@code usuario}, la petición
     * continúa hacia el servlet destino. En caso contrario, crea una sesión,
     * añade el mensaje de aviso y redirige a {@code /login}.
     * </p>
     *
     * @param req   petición HTTP entrante
     * @param res   respuesta HTTP saliente
     * @param chain cadena de filtros a continuar si el usuario está autenticado
     * @throws IOException      si ocurre un error al redirigir la respuesta
     * @throws ServletException si ocurre un error en el procesamiento del filtro
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession sesion = request.getSession(false);

        boolean logueado = sesion != null && sesion.getAttribute("usuario") != null;

        if (logueado) {
            chain.doFilter(req, res);
        } else {
            sesion = request.getSession();
            sesion.setAttribute("mensaje", "Debes iniciar sesión para acceder a esa página");
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    /**
     * Inicialización del filtro. No requiere configuración adicional.
     *
     * @param filterConfig configuración del filtro proporcionada por el contenedor
     * @throws ServletException si ocurre un error durante la inicialización
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * Destrucción del filtro. No requiere liberar recursos adicionales.
     */
    @Override
    public void destroy() {}
}