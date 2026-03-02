package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guti.dto.UsuarioSessionDTO;
import com.guti.service.AuthService;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/vistas/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        HttpSession sesion = req.getSession();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            sesion.setAttribute("mensaje", "Los campos no pueden estar vacíos");
            resp.sendRedirect("login");
            return;
        }

        try {
            UsuarioSessionDTO usuario = AuthService.login(email, password);
            sesion.setAttribute("usuario", usuario);
            resp.sendRedirect("tienda");
        } catch (Exception e) {
            sesion.setAttribute("mensaje", e.getMessage());
            resp.sendRedirect("login");
        }
    }
}
