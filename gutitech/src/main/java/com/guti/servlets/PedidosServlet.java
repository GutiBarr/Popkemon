package com.guti.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guti.beans.Pedido;
import com.guti.db.daos.PedidoDAO;
import com.guti.dto.UsuarioSessionDTO;

/**
 * Servlet que muestra el historial de pedidos finalizados del usuario.
 * <p>
 * Recupera todos los pedidos con estado {@code 'f'} del usuario autenticado
 * y los pasa a la vista {@code pedidos.jsp}. Si el usuario no está logueado,
 * redirige directamente al login, aunque la ruta {@code /pedidos} también
 * está protegida por {@link com.guti.filters.AuthFilter}.
 * </p>
 * <p>URL de acceso: {@code /pedidos}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.PedidoDAO
 * @see com.guti.filters.AuthFilter
 */
@WebServlet(urlPatterns = {"/pedidos"})
public class PedidosServlet extends HttpServlet {

    /**
     * Carga el historial de pedidos del usuario y redirige a la vista.
     * <p>
     * Comprueba que haya un usuario en sesión, obtiene sus pedidos finalizados
     * mediante {@link PedidoDAO#obtenerPedidosFinalizados} y los almacena en
     * el atributo de petición {@code pedidos} para que la vista pueda listarlos.
     * Cada pedido incluye sus líneas de detalle con el producto y la cantidad.
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

        if (usuario == null) {
            resp.sendRedirect("login");
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();
        List<Pedido> pedidos = pedidoDAO.obtenerPedidosFinalizados(usuario.getIdUsuario());

        req.setAttribute("pedidos", pedidos);
        req.getRequestDispatcher("WEB-INF/vistas/pedidos.jsp").forward(req, resp);
    }
}