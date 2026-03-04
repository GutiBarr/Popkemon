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

/**
 * Servlet que gestiona la visualización y edición del perfil de usuario.
 * <p>
 * Permite al usuario autenticado ver sus datos personales, actualizarlos
 * (incluyendo la subida de un nuevo avatar) y cambiar su contraseña.
 * </p>
 * <p>
 * La anotación {@code @MultipartConfig} habilita el soporte para formularios
 * con {@code enctype="multipart/form-data"}, necesario para la subida del avatar.
 * Los límites configurados son:
 * </p>
 * <ul>
 *   <li>Umbral de escritura en disco: 2 MB</li>
 *   <li>Tamaño máximo de archivo: 5 MB</li>
 *   <li>Tamaño máximo de la petición completa: 10 MB</li>
 * </ul>
 * <p>
 * <strong>Acciones POST disponibles</strong> (parámetro {@code accion}):
 * </p>
 * <ul>
 *   <li>{@code editarPerfil} — actualiza los datos personales y el avatar.</li>
 *   <li>{@code cambiarPassword} — verifica la contraseña actual y aplica la nueva.</li>
 * </ul>
 * <p>URL de acceso: {@code /perfil}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.UsuarioDAO
 * @see com.guti.security.PasswordHasher
 */
@WebServlet(urlPatterns = {"/perfil"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class PerfilServlet extends HttpServlet {

    /**
     * Ruta absoluta al directorio donde se almacenan los avatares de los usuarios.
     * Se construye a partir de la propiedad {@code user.home} del sistema.
     */
    private static final String RUTA_AVATARES =
            System.getProperty("user.home") + File.separator + "uploads";

    /**
     * Muestra la página de perfil con los datos actuales del usuario.
     * <p>
     * Obtiene el identificador del usuario desde la sesión, carga sus datos
     * completos desde la BD mediante {@link UsuarioDAO#obtenerPorId} y los
     * pone en el atributo de petición {@code usuario} para que la vista
     * {@code perfil.jsp} los muestre en el formulario.
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
        UsuarioSessionDTO usuarioSesion = (UsuarioSessionDTO) sesion.getAttribute("usuario");

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorId(usuarioSesion.getIdUsuario());

        req.setAttribute("usuario", usuario);
        req.getRequestDispatcher("WEB-INF/vistas/perfil.jsp").forward(req, resp);
    }

    /**
     * Procesa las acciones de edición del perfil y cambio de contraseña.
     * <p>
     * Acción {@code editarPerfil}:
     * </p>
     * <ol>
     *   <li>Lee los datos del formulario (nombre, apellidos, teléfono, dirección, etc.).</li>
     *   <li>Si se ha subido un nuevo avatar con tipo MIME válido (JPEG, PNG o WebP),
     *       genera un nombre único con {@link UUID} y lo guarda en {@link #RUTA_AVATARES},
     *       creando la carpeta si no existe.</li>
     *   <li>Actualiza el registro en BD con {@link UsuarioDAO#actualizarPerfil}.</li>
     *   <li>Refresca el {@link UsuarioSessionDTO} en sesión con los nuevos datos
     *       para que la cabecera refleje el cambio sin necesidad de hacer logout.</li>
     * </ol>
     * <p>
     * Acción {@code cambiarPassword}:
     * </p>
     * <ol>
     *   <li>Verifica que la contraseña nueva y su repetición coincidan.</li>
     *   <li>Comprueba la contraseña actual contra el hash Argon2id almacenado
     *       con {@link PasswordHasher#verifyPassword}.</li>
     *   <li>Si todo es correcto, genera el nuevo hash con {@link PasswordHasher#hashPassword}
     *       y lo actualiza en BD con {@link UsuarioDAO#actualizarPassword}.</li>
     * </ol>
     * <p>
     * En todos los casos se redirige de vuelta a {@code /perfil} con un mensaje
     * de resultado en sesión (patrón Post/Redirect/Get).
     * </p>
     *
     * @param req  petición multipart con los datos del formulario
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al guardar el archivo o redirigir
     */
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
                    nombreAvatar = UUID.randomUUID().toString().substring(0, 8) + extension;
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