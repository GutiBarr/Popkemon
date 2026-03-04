package com.guti.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet que sirve archivos de imagen subidos por los usuarios.
 * <p>
 * Mapea las peticiones {@code /media/*} a archivos reales almacenados
 * en el directorio {@code uploads} del directorio home del usuario del sistema
 * ({@code user.home/uploads}). En Windows esto equivale a
 * {@code C:\\Users\\<usuario>\\uploads}.
 * </p>
 * <p>
 * Soporta los formatos JPEG ({@code .jpg}, {@code .jpeg}), PNG ({@code .png})
 * y WebP ({@code .webp}). Cualquier otro formato se sirve como
 * {@code application/octet-stream}.
 * </p>
 * <p>
 * Ejemplos de URL:
 * </p>
 * <ul>
 *   <li>{@code /media/avatares/uuid.jpg} → sirve el avatar de un usuario</li>
 *   <li>{@code /media/imagenes/procesadores/1234567890.jpg} → sirve la imagen de un producto</li>
 * </ul>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/media/*"})
public class MediaServlet extends HttpServlet {

    /**
     * Ruta absoluta al directorio de uploads en el sistema de ficheros.
     * Se construye a partir de la propiedad {@code user.home} del sistema.
     */
    private static final String RUTA_UPLOADS =
        System.getProperty("user.home") + File.separator + "uploads";

    /**
     * Sirve un archivo de imagen desde el sistema de ficheros del servidor.
     * <p>
     * Obtiene la ruta relativa del archivo desde {@code pathInfo}, la combina
     * con {@link #RUTA_UPLOADS} y verifica que el archivo exista.
     * Determina el tipo MIME por la extensión del archivo y escribe el contenido
     * binario en la respuesta HTTP usando un buffer de 4096 bytes.
     * Devuelve HTTP 404 si el archivo no se encuentra.
     * </p>
     *
     * @param req  petición HTTP con la ruta relativa de la imagen en {@code pathInfo}
     *             (por ejemplo {@code /avatares/uuid.jpg})
     * @param resp respuesta HTTP con el contenido binario de la imagen
     *             y el {@code Content-Type} correspondiente
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al leer el archivo o escribir la respuesta
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fichero = req.getPathInfo();

        if (fichero == null || fichero.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File imagen = new File(RUTA_UPLOADS + fichero);

        if (!imagen.exists() || !imagen.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nombre = imagen.getName().toLowerCase();
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
            resp.setContentType("image/jpeg");
        } else if (nombre.endsWith(".png")) {
            resp.setContentType("image/png");
        } else if (nombre.endsWith(".webp")) {
            resp.setContentType("image/webp");
        } else {
            resp.setContentType("application/octet-stream");
        }

        resp.setContentLength((int) imagen.length());

        try (FileInputStream fis = new FileInputStream(imagen);
             OutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesLeidos;
            while ((bytesLeidos = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesLeidos);
            }
        }
    }
}
