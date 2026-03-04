package com.guti.events;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.guti.beans.Categoria;
import com.guti.db.daos.CategoriaDAO;

/**
 * Listener de contexto que se ejecuta al arrancar y detener la aplicación web.
 * <p>
 * Al iniciar la aplicación, carga todas las categorías de la base de datos
 * y las almacena en el {@link ServletContext} bajo el atributo {@code categorias}.
 * De este modo, todas las vistas JSP tienen acceso a las categorías sin necesidad
 * de consultar la BD en cada petición.
 * </p>
 * <p>
 * La comprobación {@code ctx.getAttribute("categorias") == null} evita
 * recargar las categorías si el contexto ya las tiene, por ejemplo
 * tras un redespliegue en caliente.
 * </p>
 * <p>
 * Al detener la aplicación, elimina el atributo del contexto para
 * liberar los recursos correctamente.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.CategoriaDAO
 * @see com.guti.beans.Categoria
 */
@WebListener
public class StartListener implements ServletContextListener {

    /**
     * Se ejecuta al arrancar el servidor web.
     * <p>
     * Comprueba si las categorías ya están en el contexto y, si no,
     * las carga desde la base de datos mediante {@link CategoriaDAO}
     * y las almacena en el {@link ServletContext} con el atributo
     * {@code categorias} para que sean accesibles desde todos los JSP
     * con {@code ${categorias}}.
     * </p>
     *
     * @param sce evento de inicialización del contexto de la aplicación
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        if (ctx.getAttribute("categorias") == null) {
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            List<Categoria> categorias = categoriaDAO.listarCategorias();
            ctx.setAttribute("categorias", categorias);
        }
    }

    /**
     * Se ejecuta al detener el servidor web.
     * <p>
     * Elimina el atributo {@code categorias} del {@link ServletContext}
     * para liberar la memoria ocupada por la lista al apagar la aplicación.
     * </p>
     *
     * @param sce evento de destrucción del contexto de la aplicación
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.removeAttribute("categorias");
    }
}
