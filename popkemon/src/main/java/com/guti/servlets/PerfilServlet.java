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
import com.guti.db.daos.UsuarioDAO;
import com.guti.dto.UsuarioSessionDTO;
import com.guti.security.PasswordHasher;

@WebServlet(urlPatterns = {"/perfil"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class PerfilServlet extends HttpServlet {

    private static final String RUTA_AVATARES = System.getProperty("user.home") + File.separator + "popkemon_uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuarioSesion = (UsuarioSessionDTO) sesion.getAttribute("usuario");

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorId(usuarioSesion.getIdUsuario());

        req.setAttribute("usuario", usuario);
        req.getRequestDispatcher("WEB-INF/vistas/perfil.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession();
        UsuarioSessionDTO usuarioSesion = (UsuarioSessionDTO) sesion.getAttribute("usuario");
        String accion = req.getParameter("accion");
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if ("editarPerfil".equals(accion)) {

            String nombre    = req.getParameter("nombre");
            String apellidos = req.getParameter("apellidos");
            String telefono  = req.getParameter("telefono");
            String direccion = req.getParameter("direccion");
            String cp        = req.getParameter("codigoPostal");
            String localidad = req.getParameter("localidad");
            String provincia = req.getParameter("provincia");

            // Gestión del avatar
            String nombreAvatar = usuarioSesion.getAvatar();
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
            usuario.setIdUsuario(usuarioSesion.getIdUsuario());
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuario.setCodigoPostal(cp);
            usuario.setLocalidad(localidad);
            usuario.setProvincia(provincia);
            usuario.setAvatar(nombreAvatar);

            if (usuarioDAO.actualizarPerfil(usuario)) {
                // Actualizamos la sesion con los nuevos datos
                usuarioSesion.setNombre(nombre);
                usuarioSesion.setApellidos(apellidos);
                usuarioSesion.setAvatar(nombreAvatar);
                sesion.setAttribute("usuario", usuarioSesion);
                sesion.setAttribute("mensaje", "Perfil actualizado correctamente");
            } else {
                sesion.setAttribute("mensaje", "Error al actualizar el perfil");
            }

            resp.sendRedirect("perfil");

        } else if ("cambiarPassword".equals(accion)) {

            String passwordActual = req.getParameter("passwordActual");
            String passwordNueva  = req.getParameter("passwordNueva");
            String passwordRep    = req.getParameter("passwordRep");

            if (!passwordNueva.equals(passwordRep)) {
                sesion.setAttribute("mensaje", "Las contrasenas nuevas no coinciden");
                resp.sendRedirect("perfil");
                return;
            }

            Usuario usuario = usuarioDAO.obtenerPorId(usuarioSesion.getIdUsuario());

            if (!PasswordHasher.verifyPassword(usuario.getPassword(), passwordActual.toCharArray())) {
                sesion.setAttribute("mensaje", "La contrasena actual no es correcta");
                resp.sendRedirect("perfil");
                return;
            }

            String nuevoHash = PasswordHasher.hashPassword(passwordNueva.toCharArray());
            if (usuarioDAO.actualizarPassword(usuarioSesion.getIdUsuario(), nuevoHash)) {
                sesion.setAttribute("mensaje", "Contrasena cambiada correctamente");
            } else {
                sesion.setAttribute("mensaje", "Error al cambiar la contrasena");
            }

            resp.sendRedirect("perfil");
        }
    }
}