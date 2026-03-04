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

/**
 * Servlet que gestiona las peticiones Ajax de la aplicación.
 * <p>
 * Centraliza las llamadas asíncronas desde el cliente, devolviendo siempre
 * respuestas en formato JSON ({@code application/json}).
 * </p>
 * <p>
 * <strong>Acciones GET disponibles</strong> (parámetro {@code accion}):
 * </p>
 * <ul>
 *   <li>{@code comprobarEmail} — comprueba si un email ya está registrado.
 *       Devuelve {@code {"existe": true/false}}.</li>
 *   <li>{@code calcularLetraNif} — calcula la letra del NIF a partir de los 8 dígitos.
 *       Devuelve {@code {"letra": "X"}}.</li>
 * </ul>
 * <p>
 * <strong>Acciones POST disponibles</strong> (parámetro {@code accion},
 * requieren usuario en sesión):
 * </p>
 * <ul>
 *   <li>{@code aumentar} — incrementa en 1 la cantidad de una línea del carrito.</li>
 *   <li>{@code disminuir} — decrementa en 1 la cantidad de una línea (mínimo 1).</li>
 * </ul>
 * <p>
 * Las acciones POST devuelven {@code {"cantidad": N, "total": X.XX}} con la
 * nueva cantidad de la línea y el importe total actualizado del carrito.
 * Si el usuario no está autenticado, responde con HTTP 401 y
 * {@code {"error": "No autenticado"}}.
 * </p>
 * <p>URL de acceso: {@code /ajax}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.db.daos.PedidoDAO
 * @see com.guti.db.daos.UsuarioDAO
 */
@WebServlet(urlPatterns = {"/ajax"})
public class AjaxServlet extends HttpServlet {

    /** Instancia de Gson reutilizada para serializar las respuestas JSON. */
    Gson gson = new Gson();

    /**
     * Procesa las peticiones Ajax GET.
     * <p>
     * Acciones disponibles:
     * </p>
     * <ul>
     *   <li>{@code comprobarEmail}: recibe el parámetro {@code email} y consulta
     *       si ya existe en la BD mediante {@link UsuarioDAO#emailExiste}.
     *       Devuelve {@code {"existe": true/false}}.</li>
     *   <li>{@code calcularLetraNif}: recibe el parámetro {@code numeros} (8 dígitos)
     *       y calcula la letra del NIF usando el algoritmo oficial (módulo 23).
     *       Devuelve {@code {"letra": "X"}} o {@code {"error": "..."}} si el número
     *       no es válido.</li>
     * </ul>
     * <p>
     * Si la acción no es reconocida, responde con HTTP 400 (Bad Request).
     * </p>
     *
     * @param req  petición HTTP con los parámetros {@code accion} y los datos necesarios
     * @param resp respuesta HTTP con el JSON resultante
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al escribir la respuesta
     */
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

        if ("calcularLetraNif".equals(accion)) {
            String numeros = req.getParameter("numeros");
            JsonObject json = new JsonObject();
            try {
                int num = Integer.parseInt(numeros);
                String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
                String letra = String.valueOf(letras.charAt(num % 23));
                json.addProperty("letra", letra);
            } catch (NumberFormatException e) {
                json.addProperty("error", "Numero invalido");
            }
            resp.getWriter().write(gson.toJson(json));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Procesa las peticiones Ajax POST relacionadas con el carrito.
     * <p>
     * Todas las acciones POST requieren que el usuario esté autenticado.
     * Si no hay sesión activa, responde con HTTP 401.
     * </p>
     * <p>
     * Acciones disponibles:
     * </p>
     * <ul>
     *   <li>{@code aumentar}: incrementa en 1 la cantidad de la línea indicada por
     *       {@code idLinea}. Recalcula el importe total del carrito y devuelve
     *       {@code {"cantidad": N, "total": X.XX}}.</li>
     *   <li>{@code disminuir}: decrementa en 1 la cantidad de la línea indicada por
     *       {@code idLinea}. Si la cantidad actual es 1, no la reduce más.
     *       Devuelve {@code {"cantidad": N, "total": X.XX}}.</li>
     * </ul>
     * <p>
     * Si la acción no es reconocida, responde con HTTP 400 (Bad Request).
     * </p>
     *
     * @param req  petición HTTP con los parámetros {@code accion}, {@code idLinea}
     *             y {@code cantidad}
     * @param resp respuesta HTTP con el JSON resultante
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error al escribir la respuesta
     */
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