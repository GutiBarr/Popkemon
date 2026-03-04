package com.guti.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guti.beans.Pedido;
import com.guti.beans.Producto;
import com.guti.db.daos.PedidoDAO;
import com.guti.db.daos.ProductoDAO;
import com.guti.dto.UsuarioSessionDTO;
import com.guti.utils.CarritoAnonimo;
import com.guti.utils.CarritoAnonimo.LineaAnonima;

/**
 * Servlet que gestiona el carrito de compra del usuario.
 * <p>
 * Soporta dos modos de funcionamiento según el estado de autenticación:
 * </p>
 * <ul>
 *   <li><strong>Usuario logueado:</strong> el carrito se persiste en la base de datos
 *       mediante {@link PedidoDAO}. Un pedido en estado {@code 'c'} actúa como carrito.</li>
 *   <li><strong>Usuario anónimo:</strong> el carrito se almacena en una cookie JSON
 *       mediante {@link CarritoAnonimo}.</li>
 * </ul>
 * <p>
 * <strong>Acciones POST disponibles</strong> (parámetro {@code accion}):
 * </p>
 * <ul>
 *   <li>{@code anadir} — añade un producto al carrito. Crea el carrito en BD si no existe.</li>
 *   <li>{@code eliminarLinea} — elimina una línea del carrito (por {@code idLinea} si logueado,
 *       por {@code idProducto} si anónimo).</li>
 *   <li>{@code vaciar} — vacía completamente el carrito.</li>
 *   <li>{@code finalizar} — finaliza el pedido (solo usuarios logueados) y redirige a
 *       {@code /pedidos}.</li>
 * </ul>
 * <p>URL de acceso: {@code /carrito}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.PedidoDAO
 * @see com.guti.utils.CarritoAnonimo
 */
@WebServlet(urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    /**
     * Muestra el contenido del carrito del usuario.
     * <p>
     * Si el usuario está logueado, carga el carrito activo desde la BD
     * y lo pone en el atributo de petición {@code carrito}.
     * Si es anónimo, lee la cookie, carga los objetos {@link Producto}
     * correspondientes desde {@link ProductoDAO} y los pone en los atributos
     * {@code carritoAnonimo} (lista de {@link LineaAnonima}) y
     * {@code productosAnonimos} (lista de {@link Producto}).
     * En ambos casos redirige a la vista {@code carrito.jsp}.
     * </p>
     *
     * @param req  petición HTTP
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuario = (UsuarioSessionDTO) sesion.getAttribute("usuario");

        if (usuario != null) {
            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            req.setAttribute("carrito", carrito);
        } else {
            List<LineaAnonima> lineasAnonimas = CarritoAnonimo.leer(req);
            List<Producto> productosAnonimos = new ArrayList<>();
            ProductoDAO productoDAO = new ProductoDAO();
            for (LineaAnonima linea : lineasAnonimas) {
                Producto p = productoDAO.obtenerPorId(linea.getIdProducto());
                if (p != null) {
                    productosAnonimos.add(p);
                }
            }
            req.setAttribute("carritoAnonimo", lineasAnonimas);
            req.setAttribute("productosAnonimos", productosAnonimos);
        }

        req.getRequestDispatcher("WEB-INF/vistas/carrito.jsp").forward(req, resp);
    }

    /**
     * Procesa las acciones sobre el carrito: añadir, eliminar, vaciar y finalizar.
     * <p>
     * El comportamiento varía según si el usuario está autenticado o es anónimo,
     * excepto la acción {@code finalizar} que solo está disponible para usuarios logueados.
     * Tras cada acción se realiza una redirección para evitar el reenvío del formulario
     * al recargar la página (patrón Post/Redirect/Get).
     * </p>
     *
     * @param req  petición HTTP con los parámetros {@code accion} y los datos necesarios
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuario = (UsuarioSessionDTO) sesion.getAttribute("usuario");
        String accion = req.getParameter("accion");
        PedidoDAO pedidoDAO = new PedidoDAO();

        if ("anadir".equals(accion)) {
            int idProducto = Integer.parseInt(req.getParameter("idProducto"));
            int cantidad   = Integer.parseInt(req.getParameter("cantidad"));

            if (usuario == null) {
                CarritoAnonimo.anadir(req, resp, idProducto, cantidad);
                sesion.setAttribute("mensaje", "Producto anadido al carrito");
                resp.sendRedirect("carrito");
                return;
            }

            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            if (carrito == null) {
                carrito = pedidoDAO.crearCarrito(usuario.getIdUsuario());
            }

            pedidoDAO.anadirLinea(carrito.getIdPedido(), idProducto, cantidad);
            sesion.setAttribute("mensaje", "Producto anadido al carrito");
            resp.sendRedirect("carrito");

        } else if ("eliminarLinea".equals(accion)) {
            if (usuario == null) {
                int idProducto = Integer.parseInt(req.getParameter("idProducto"));
                CarritoAnonimo.eliminar(req, resp, idProducto);
            } else {
                int idLinea = Integer.parseInt(req.getParameter("idLinea"));
                pedidoDAO.eliminarLinea(idLinea);
            }
            resp.sendRedirect("carrito");

        } else if ("vaciar".equals(accion)) {
            if (usuario == null) {
                CarritoAnonimo.vaciar(resp);
            } else {
                Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
                if (carrito != null) {
                    pedidoDAO.vaciarCarrito(carrito.getIdPedido());
                }
            }
            resp.sendRedirect("carrito");

        } else if ("finalizar".equals(accion)) {
            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            if (carrito != null && !carrito.getLineas().isEmpty()) {
                pedidoDAO.finalizarPedido(carrito.getIdPedido());
                sesion.setAttribute("mensaje", "Pedido realizado correctamente");
            }
            resp.sendRedirect("pedidos");
        }
    }
}