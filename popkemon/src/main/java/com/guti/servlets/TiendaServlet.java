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

@WebServlet(urlPatterns = {"/tienda"})
public class TiendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ProductoDAO productoDAO = new ProductoDAO();
        List<Producto> productos;

        String busqueda = req.getParameter("busqueda");
        String idCategoria = req.getParameter("idCategoria");

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoDAO.buscarPorNombre(busqueda.trim());
            req.setAttribute("busqueda", busqueda.trim());
        } else if (idCategoria != null && !idCategoria.isEmpty()) {
            productos = productoDAO.listarPorCategoria(Integer.parseInt(idCategoria));
            req.setAttribute("idCategoria", Integer.parseInt(idCategoria));
        } else {
            productos = productoDAO.listarProductos();
        }

        req.setAttribute("productos", productos);
        req.getRequestDispatcher("WEB-INF/vistas/tienda.jsp").forward(req, resp);
    }
}
