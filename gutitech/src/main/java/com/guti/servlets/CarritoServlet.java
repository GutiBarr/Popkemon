package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guti.beans.Pedido;
import com.guti.db.daos.PedidoDAO;
import com.guti.dto.UsuarioSessionDTO;

@WebServlet(urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

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
        Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());

        req.setAttribute("carrito", carrito);
        req.getRequestDispatcher("WEB-INF/vistas/carrito.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuario = (UsuarioSessionDTO) sesion.getAttribute("usuario");

        if (usuario == null) {
            resp.sendRedirect("login");
            return;
        }

        String accion = req.getParameter("accion");
        PedidoDAO pedidoDAO = new PedidoDAO();

        if ("anadir".equals(accion)) {
            int idProducto = Integer.parseInt(req.getParameter("idProducto"));
            int cantidad   = Integer.parseInt(req.getParameter("cantidad"));

            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            if (carrito == null) {
                carrito = pedidoDAO.crearCarrito(usuario.getIdUsuario());
            }

            pedidoDAO.anadirLinea(carrito.getIdPedido(), idProducto, cantidad);
            sesion.setAttribute("mensaje", "Producto anadido al carrito");
            resp.sendRedirect("carrito");

        } else if ("eliminarLinea".equals(accion)) {
            int idLinea = Integer.parseInt(req.getParameter("idLinea"));
            pedidoDAO.eliminarLinea(idLinea);
            resp.sendRedirect("carrito");

        } else if ("vaciar".equals(accion)) {
            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            if (carrito != null) {
                pedidoDAO.vaciarCarrito(carrito.getIdPedido());
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
