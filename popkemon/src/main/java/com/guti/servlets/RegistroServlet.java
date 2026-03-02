package com.guti.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.guti.beans.Usuario;
import com.guti.service.AuthService;

@WebServlet(urlPatterns = {"/registro"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class RegistroServlet extends HttpServlet {

    private static final String RUTA_AVATARES = System.getProperty("user.home") + File.separator + "popkemon_uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/vistas/registro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();

        String email       = req.getParameter("email");
        String password    = req.getParameter("password");
        String passwordRep = req.getParameter("passwordRep");
        String nombre      = req.getParameter("nombre");
        String apellidos   = req.getParameter("apellidos");
        String nif         = req.getParameter("nif");
        String telefono    = req.getParameter("telefono");
        String direccion   = req.getParameter("direccion");
        String cp          = req.getParameter("codigoPostal");
        String localidad   = req.getParameter("localidad");
        String provincia   = req.getParameter("provincia");

        // Validaciones básicas servidor
        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() ||
            apellidos.isEmpty() || nif.isEmpty() || direccion.isEmpty() ||
            cp.isEmpty() || localidad.isEmpty() || provincia.isEmpty()) {
            sesion.setAttribute("mensaje", "Todos los campos obligatorios deben estar rellenos");
            resp.sendRedirect("registro");
            return;
        }

        if (!password.equals(passwordRep)) {
            sesion.setAttribute("mensaje", "Las contraseñas no coinciden");
            resp.sendRedirect("registro");
            return;
        }

        // Gestión del avatar
        String nombreAvatar = null;
        Part avatar = req.getPart("avatar");
        if (avatar != null && avatar.getSize() > 0) {
            String mime = avatar.getContentType();
            if (mime.equals("image/jpeg") || mime.equals("image/png") || mime.equals("image/webp")) {
                String extension = mime.equals("image/png") ? ".png" : mime.equals("image/webp") ? ".webp" : ".jpg";
                nombreAvatar = UUID.randomUUID().toString() + extension;
                File carpeta = new File(RUTA_AVATARES);
                if (!carpeta.exists()) carpeta.mkdirs();
                Path destino = Paths.get(RUTA_AVATARES, nombreAvatar);
                try (InputStream input = avatar.getInputStream()) {
                    Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setNif(nif);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setCodigoPostal(cp);
        usuario.setLocalidad(localidad);
        usuario.setProvincia(provincia);
        usuario.setAvatar(nombreAvatar);

        if (!AuthService.registrar(usuario)) {
            sesion.setAttribute("mensaje", "El email ya está registrado");
            resp.sendRedirect("registro");
            return;
        }

        sesion.setAttribute("mensaje", "Registro completado. Ya puedes iniciar sesión");
        resp.sendRedirect("login");
    }
}
