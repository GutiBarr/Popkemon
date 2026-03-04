package com.guti.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guti.beans.Producto;
import com.guti.db.daos.ProductoDAO;

/**
 * Servlet principal de la tienda que gestiona el catálogo de productos.
 * <p>
 * Responde a peticiones GET mostrando el grid de productos con soporte
 * para múltiples filtros combinados. Todos los filtros son opcionales;
 * si no se envía ninguno, se muestran todos los productos.
 * </p>
 * <p>
 * <strong>Parámetros GET disponibles:</strong>
 * </p>
 * <ul>
 *   <li>{@code busqueda} — texto libre a buscar en nombre o descripción.</li>
 *   <li>{@code idCategoria} — identificador de categoría para filtrar.</li>
 *   <li>{@code marca} — marca exacta a filtrar.</li>
 *   <li>{@code precioMin} — precio mínimo del rango.</li>
 *   <li>{@code precioMax} — precio máximo del rango.</li>
 *   <li>{@code orden} — criterio de ordenación: {@code precioAsc},
 *       {@code precioDesc} o {@code nombre}.</li>
 * </ul>
 * <p>
 * Los atributos del filtro se reenvían a la vista para que los campos
 * del formulario mantengan los valores seleccionados tras la búsqueda.
 * </p>
 * <p>URL de acceso: {@code /tienda}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.ProductoDAO
 */
@WebServlet(urlPatterns = {"/tienda"})
public class TiendaServlet extends HttpServlet {

    /**
     * Procesa los filtros de búsqueda, carga los productos y redirige a la vista.
     * <p>
     * Lee los parámetros de filtro de la petición, convierte los tipos necesarios
     * (los parámetros numéricos llegan como {@code String} y se parsean a
     * {@code Integer} y {@code Double}) y llama a
     * {@link ProductoDAO#buscarConFiltros} con los valores obtenidos.
     * También carga la lista de marcas disponibles para poblar el selector
     * del formulario. Todos los parámetros del filtro se ponen como atributos
     * de petición para que la vista pueda repintar el formulario con los valores
     * activos.
     * </p>
     *
     * @param req  petición HTTP con los parámetros de filtro opcionales
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ProductoDAO productoDAO = new ProductoDAO();

        String busqueda   = req.getParameter("busqueda");
        String idCatParam = req.getParameter("idCategoria");
        String marca      = req.getParameter("marca");
        String precioMinP = req.getParameter("precioMin");
        String precioMaxP = req.getParameter("precioMax");
        String orden      = req.getParameter("orden");

        Integer idCategoria = null;
        Double precioMin = null;
        Double precioMax = null;

        if (idCatParam != null && !idCatParam.isEmpty()) {
            idCategoria = Integer.parseInt(idCatParam);
        }
        if (precioMinP != null && !precioMinP.isEmpty()) {
            precioMin = Double.parseDouble(precioMinP);
        }
        if (precioMaxP != null && !precioMaxP.isEmpty()) {
            precioMax = Double.parseDouble(precioMaxP);
        }

        List<Producto> productos = productoDAO.buscarConFiltros(
                busqueda, idCategoria, marca, precioMin, precioMax, orden);

        List<String> marcas = productoDAO.listarMarcas();

        req.setAttribute("productos", productos);
        req.setAttribute("marcas", marcas);
        req.setAttribute("busqueda", busqueda);
        req.setAttribute("idCategoria", idCategoria);
        req.setAttribute("marca", marca);
        req.setAttribute("precioMin", precioMinP);
        req.setAttribute("precioMax", precioMaxP);
        req.setAttribute("orden", orden);

        req.getRequestDispatcher("WEB-INF/vistas/tienda.jsp").forward(req, resp);
    }
}
