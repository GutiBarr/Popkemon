package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guti.beans.Pedido;
import com.guti.db.daos.PedidoDAO;
import com.guti.db.daos.UsuarioDAO;
import com.guti.dto.UsuarioSessionDTO;

@WebServlet(urlPatterns = {"/ajax"})
public class AjaxServlet extends HttpServlet {

    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String accion = req.getParameter("accion");

        if ("comprobarEmail".equals(accion)) {
            String email = req.getParameter("email");
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean existe = usuarioDAO.emailExiste(email);
            JsonObject json = new JsonObject();
            json.addProperty("existe", existe);
            resp.getWriter().write(gson.toJson(json));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String accion = req.getParameter("accion");
        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuario = (UsuarioSessionDTO) sesion.getAttribute("usuario");

        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject error = new JsonObject();
            error.addProperty("error", "No autenticado");
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();

        if ("aumentar".equals(accion)) {
            int idLinea = Integer.parseInt(req.getParameter("idLinea"));
            int cantidadActual = Integer.parseInt(req.getParameter("cantidad"));
            pedidoDAO.actualizarCantidad(idLinea, cantidadActual + 1);
            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            JsonObject json = new JsonObject();
            json.addProperty("cantidad", cantidadActual + 1);
            json.addProperty("total", carrito.getImporte());
            resp.getWriter().write(gson.toJson(json));

        } else if ("disminuir".equals(accion)) {
            int idLinea = Integer.parseInt(req.getParameter("idLinea"));
            int cantidadActual = Integer.parseInt(req.getParameter("cantidad"));
            if (cantidadActual > 1) {
                pedidoDAO.actualizarCantidad(idLinea, cantidadActual - 1);
            }
            Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
            JsonObject json = new JsonObject();
            json.addProperty("cantidad", cantidadActual > 1 ? cantidadActual - 1 : 1);
            json.addProperty("total", carrito.getImporte());
            resp.getWriter().write(gson.toJson(json));

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
