package com.guti.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.guti.beans.Pedido;
import com.guti.db.daos.PedidoDAO;
import com.guti.dto.UsuarioSessionDTO;
import com.guti.service.AuthService;
import com.guti.utils.CarritoAnonimo;

/**
 * Servlet que gestiona el inicio de sesión de usuarios.
 * <p>
 * El GET muestra el formulario de login. El POST procesa las credenciales,
 * crea la sesión y, si el usuario tenía productos en el carrito anónimo
 * (cookie), los fusiona con su carrito en la base de datos antes de redirigir
 * a la tienda.
 * </p>
 * <p>URL de acceso: {@code /login}</p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.service.AuthService
 * @see com.guti.utils.CarritoAnonimo
 */
@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /**
     * Muestra el formulario de inicio de sesión.
     * <p>
     * Redirige a la vista {@code login.jsp}. Si en la sesión existe el atributo
     * {@code mensaje} (puesto por una redirección anterior), la vista lo mostrará
     * como aviso al usuario.
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
        req.getRequestDispatcher("WEB-INF/vistas/login.jsp").forward(req, resp);
    }

    /**
     * Procesa el formulario de inicio de sesión.
     * <p>
     * Flujo de ejecución:
     * </p>
     * <ol>
     *   <li>Valida que los campos {@code email} y {@code password} no estén vacíos.</li>
     *   <li>Llama a {@link AuthService#login} para verificar las credenciales con Argon2id.</li>
     *   <li>Si el login es correcto, guarda el {@link UsuarioSessionDTO} en la sesión
     *       bajo el atributo {@code usuario}.</li>
     *   <li>Comprueba si existe un carrito anónimo en la cookie. Si hay líneas,
     *       las fusiona con el carrito del usuario en BD (creándolo si no existe)
     *       y elimina la cookie.</li>
     *   <li>Redirige a {@code /tienda} si todo es correcto, o de vuelta a {@code /login}
     *       con un mensaje de error si las credenciales son incorrectas.</li>
     * </ol>
     *
     * @param req  petición HTTP con los parámetros {@code email} y {@code password}
     * @param resp respuesta HTTP
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        HttpSession sesion = req.getSession();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            sesion.setAttribute("mensaje", "Los campos no pueden estar vacios");
            resp.sendRedirect("login");
            return;
        }

        try {
            UsuarioSessionDTO usuario = AuthService.login(email, password);
            sesion.setAttribute("usuario", usuario);

            // Fusionar carrito anonimo con carrito de BD
            List<CarritoAnonimo.LineaAnonima> lineasAnonimas = CarritoAnonimo.leer(req);
            if (!lineasAnonimas.isEmpty()) {
                PedidoDAO pedidoDAO = new PedidoDAO();
                Pedido carrito = pedidoDAO.obtenerCarrito(usuario.getIdUsuario());
                if (carrito == null) {
                    carrito = pedidoDAO.crearCarrito(usuario.getIdUsuario());
                }
                for (CarritoAnonimo.LineaAnonima linea : lineasAnonimas) {
                    pedidoDAO.anadirLinea(carrito.getIdPedido(), linea.idProducto, linea.cantidad);
                }
                CarritoAnonimo.vaciar(resp);
            }

            resp.sendRedirect("tienda");
        } catch (Exception e) {
            sesion.setAttribute("mensaje", e.getMessage());
            resp.sendRedirect("login");
        }
    }
}
