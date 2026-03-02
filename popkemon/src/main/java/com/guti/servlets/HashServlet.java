package com.guti.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guti.security.PasswordHasher;

@WebServlet(urlPatterns = {"/hash"})
public class HashServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String hash = PasswordHasher.hashPassword("Pokemon123!".toCharArray());
        resp.getWriter().write(hash);
    }
}