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

/**
 * Servlet que gestiona el registro de nuevos usuarios.
 * <p>
 * El GET muestra el formulario de registro. El POST valida los datos
 * en el servidor, gestiona la subida opcional del avatar y delega
 * el hashing de la contraseña e inserción en BD a {@link AuthService#registrar}.
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
 * <p>URL de acceso: {@code /registro}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.service.AuthService
 */
@WebServlet(urlPatterns = {"/registro"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class RegistroServlet extends HttpServlet {

    /**
     * Ruta absoluta al directorio donde se almacenan los avatares subidos
     * durante el registro. Se construye a partir de la propiedad
     * {@code user.home} del sistema.
     */
    private static final String RUTA_AVATARES =
            System.getProperty("user.home") + File.separator + "popkemon_uploads";

    /**
     * Muestra el formulario de registro de nuevos usuarios.
     *
     * @param req  petición HTTP
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/vistas/registro.jsp").forward(req, resp);
    }

    /**
     * Procesa el formulario de registro de un nuevo usuario.
     * <p>
     * Flujo de ejecución:
     * </p>
     * <ol>
     *   <li>Valida en el servidor que todos los campos obligatorios estén rellenos.</li>
     *   <li>Comprueba que las contraseñas introducidas coincidan.</li>
     *   <li>Si se ha subido un avatar con tipo MIME válido (JPEG, PNG o WebP),
     *       genera un nombre único con {@link UUID} y lo guarda en
     *       {@link #RUTA_AVATARES}, creando la carpeta si no existe.</li>
     *   <li>Construye el objeto {@link Usuario} con los datos del formulario
     *       y la contraseña aún en texto plano.</li>
     *   <li>Llama a {@link AuthService#registrar}, que comprueba si el email
     *       ya existe, hashea la contraseña con Argon2id e inserta el usuario en BD.</li>
     *   <li>Si el email ya está registrado, redirige al formulario con un mensaje de error.</li>
     *   <li>Si el registro es correcto, redirige al login con un mensaje de confirmación.</li>
     * </ol>
     * <p>
     * En todos los casos se aplica el patrón Post/Redirect/Get para evitar
     * reenvíos del formulario al recargar la página.
     * </p>
     *
     * @param req  petición multipart con todos los campos del formulario de registro
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al guardar el avatar o redirigir
     */
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
