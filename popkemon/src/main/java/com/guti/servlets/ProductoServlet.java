package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guti.beans.Producto;
import com.guti.db.daos.ProductoDAO;

@WebServlet(urlPatterns = {"/producto"})
public class ProductoServlet extends HttpServlet {

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
