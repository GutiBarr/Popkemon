package com.guti.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Filtro que aplica la codificación UTF-8 a todas las peticiones y respuestas.
 * <p>
 * Se aplica a todas las URLs ({@code /*}) y garantiza que los caracteres
 * especiales del español (tildes, ñ, etc.) se procesen correctamente
 * tanto en los datos recibidos de los formularios como en las respuestas
 * generadas por los servlets y JSP.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
@WebFilter(urlPatterns = {"/*"})
public class EncodingFilter implements Filter {

    /**
     * Establece la codificación UTF-8 en la petición y la respuesta antes
     * de pasarlas al siguiente elemento de la cadena de filtros.
     *
     * @param req   petición HTTP entrante
     * @param res   respuesta HTTP saliente
     * @param chain cadena de filtros a continuar
     * @throws IOException      si ocurre un error de entrada/salida
     * @throws ServletException si ocurre un error en el procesamiento del filtro
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
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