package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guti.beans.Producto;
import com.guti.db.daos.ProductoDAO;

/**
 * Servlet que muestra la página de detalle de un producto concreto.
 * <p>
 * Recibe el parámetro {@code id} por GET, carga el producto desde la BD
 * mediante {@link ProductoDAO#obtenerPorId} y lo pasa a la vista
 * {@code producto.jsp} a través del atributo de petición {@code producto}.
 * </p>
 * <p>
 * Casos de error contemplados:
 * </p>
 * <ul>
 *   <li>Si el parámetro {@code id} no se recibe o está vacío, redirige a {@code /tienda}.</li>
 *   <li>Si no existe ningún producto con ese identificador, devuelve HTTP 404.</li>
 * </ul>
 * <p>URL de acceso: {@code /producto?id=X}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.ProductoDAO
 */
@WebServlet(urlPatterns = {"/producto"})
public class ProductoServlet extends HttpServlet {

    /**
     * Carga el detalle de un producto y redirige a su vista.
     * <p>
     * Lee el parámetro {@code id} de la petición, consulta el producto en BD
     * y lo almacena en el atributo de petición {@code producto} antes de
     * hacer el forward a {@code producto.jsp}.
     * </p>
     *
     * @param req  petición HTTP con el parámetro {@code id} del producto
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect("tienda");
            return;
        }

        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.obtenerPorId(Integer.parseInt(idParam));

        if (producto == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("producto", producto);
        req.getRequestDispatcher("WEB-INF/vistas/producto.jsp").forward(req, resp);
    }
}