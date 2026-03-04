package com.guti.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Filtro que bloquea el acceso directo a los archivos JSP desde el navegador.
 * <p>
 * Se aplica a todas las URLs que terminan en {@code *.jsp}. Si alguien intenta
 * acceder directamente a un JSP (por ejemplo {@code /vistas/tienda.jsp}),
 * este filtro intercepta la petición y redirige al servlet principal
 * {@code /tienda}, garantizando que las vistas solo se sirvan a través
 * de sus servlets correspondientes y nunca de forma directa.
 * </p>
 * <p>
 * Esto evita mostrar páginas sin datos o con errores por saltarse
 * el flujo normal de la aplicación.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
@WebFilter(urlPatterns = {"*.jsp"})
public class JSPFilter implements Filter {

    /**
     * Redirige cualquier acceso directo a un JSP hacia el servlet {@code /tienda}.
     * <p>
     * La petición original no se pasa a la cadena de filtros; en su lugar
     * se envía una redirección HTTP al cliente hacia la URL {@code tienda}.
     * </p>
     *
     * @param req   petición HTTP entrante con destino a un archivo JSP
     * @param res   respuesta HTTP saliente
     * @param chain cadena de filtros (no se invoca en este filtro)
     * @throws IOException      si ocurre un error al redirigir la respuesta
     * @throws ServletException si ocurre un error en el procesamiento del filtro
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) res;
        resp.sendRedirect("tienda");
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