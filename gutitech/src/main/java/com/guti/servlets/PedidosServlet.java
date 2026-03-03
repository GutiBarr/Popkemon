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

@WebServlet(urlPatterns = {"/pedidos"})
public class PedidosServlet extends HttpServlet {

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