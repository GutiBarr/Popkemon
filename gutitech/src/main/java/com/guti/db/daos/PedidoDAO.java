package com.guti.db.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.guti.beans.Categoria;
import com.guti.beans.LineaPedido;
import com.guti.beans.Pedido;
import com.guti.beans.Producto;
import com.guti.db.Conexion;

/**
 * DAO para la gestión de pedidos y líneas de pedido en la base de datos.
 * <p>
 * Gestiona el ciclo de vida completo de un pedido: creación del carrito,
 * añadir y eliminar líneas, actualizar cantidades, vaciar el carrito
 * y finalizar el pedido. Todas las operaciones de escritura se ejecutan
 * dentro de transacciones explícitas con {@code setAutoCommit(false)}.
 * </p>
 * <p>
 * Un pedido en estado {@code 'c'} es el carrito activo del usuario.
 * Al confirmar la compra el estado cambia a {@code 'f'} (finalizado).
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.beans.Pedido
 * @see com.guti.beans.LineaPedido
 * @see com.guti.db.Conexion
 */
public class PedidoDAO {

    /**
     * Obtiene el carrito activo (estado {@code 'c'}) de un usuario.
     * <p>
     * Carga también las líneas del pedido mediante {@link #obtenerLineas},
     * que hace un JOIN con {@code productos} y {@code categorias}.
     * </p>
     *
     * @param idUsuario identificador del usuario
     * @return el pedido en estado carrito con sus líneas cargadas,
     *         o {@code null} si el usuario no tiene carrito abierto
     */
    public Pedido obtenerCarrito(int idUsuario) {

        String sql = """
                    SELECT IdPedido, Fecha, Estado, IdUsuario, Importe, Iva
                    FROM pedidos
                    WHERE IdUsuario = ? AND Estado = 'c'
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("IdPedido"),
                        rs.getDate("Fecha") != null ? rs.getDate("Fecha").toLocalDate() : null,
                        rs.getString("Estado").charAt(0),
                        rs.getInt("IdUsuario"),
                        rs.getDouble("Importe"),
                        rs.getDouble("Iva"));

                pedido.setLineas(obtenerLineas(con, pedido.getIdPedido()));
                return pedido;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Recupera el historial de pedidos finalizados de un usuario.
     * <p>
     * Devuelve los pedidos con estado {@code 'f'} ordenados por fecha
     * descendente. Cada pedido incluye sus líneas de detalle.
     * </p>
     *
     * @param idUsuario identificador del usuario
     * @return lista de pedidos finalizados con sus líneas,
     *         o lista vacía si no tiene ninguno
     */
    public List<Pedido> obtenerPedidosFinalizados(int idUsuario) {

        String sql = """
                    SELECT IdPedido, Fecha, Estado, IdUsuario, Importe, Iva
                    FROM pedidos
                    WHERE IdUsuario = ? AND Estado = 'f'
                    ORDER BY Fecha DESC
                """;

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("IdPedido"),
                        rs.getDate("Fecha") != null ? rs.getDate("Fecha").toLocalDate() : null,
                        rs.getString("Estado").charAt(0),
                        rs.getInt("IdUsuario"),
                        rs.getDouble("Importe"),
                        rs.getDouble("Iva"));

                pedido.setLineas(obtenerLineas(con, pedido.getIdPedido()));
                pedidos.add(pedido);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    /**
     * Crea un nuevo carrito vacío para un usuario.
     * <p>
     * Inserta un pedido con estado {@code 'c'}, fecha actual e importes a cero.
     * Usa {@code RETURN_GENERATED_KEYS} para recuperar el identificador
     * generado automáticamente por la base de datos.
     * </p>
     *
     * @param idUsuario identificador del usuario
     * @return el nuevo pedido creado, o {@code null} si la inserción falla
     */
    public Pedido crearCarrito(int idUsuario) {

        String sql = """
                    INSERT INTO pedidos (Fecha, Estado, IdUsuario, Importe, Iva)
                    VALUES (?, 'c', ?, 0, 0)
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            con.setAutoCommit(false);

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idUsuario);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idPedido = rs.getInt(1);
                con.commit();
                return new Pedido(idPedido, LocalDate.now(), 'c', idUsuario, 0, 0);
            }

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Añade un producto al carrito o incrementa su cantidad si ya existe.
     * <p>
     * Comprueba primero si el producto ya tiene línea en el pedido.
     * Si existe, suma la cantidad indicada. Si no, inserta una nueva línea.
     * Al terminar recalcula el importe total del pedido con {@link #actualizarImporte}.
     * </p>
     *
     * @param idPedido   identificador del pedido (carrito)
     * @param idProducto identificador del producto a añadir
     * @param cantidad   número de unidades a añadir
     * @return {@code true} si la operación fue correcta, {@code false} si hubo error
     */
    public boolean añadirLinea(int idPedido, int idProducto, int cantidad) {

        // Si ya existe la línea, sumamos cantidad
        String sqlCheck  = "SELECT IdLinea, Cantidad FROM lineaspedidos WHERE IdPedido = ? AND IdProducto = ?";
        String sqlUpdate = "UPDATE lineaspedidos SET Cantidad = Cantidad + ? WHERE IdLinea = ?";
        String sqlInsert = "INSERT INTO lineaspedidos (IdPedido, IdProducto, Cantidad) VALUES (?, ?, ?)";

        try (Connection con = Conexion.getConexion()) {

            con.setAutoCommit(false);

            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setInt(1, idPedido);
            psCheck.setInt(2, idProducto);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                psUpdate.setInt(1, cantidad);
                psUpdate.setInt(2, rs.getInt("IdLinea"));
                psUpdate.executeUpdate();
            } else {
                PreparedStatement psInsert = con.prepareStatement(sqlInsert);
                psInsert.setInt(1, idPedido);
                psInsert.setInt(2, idProducto);
                psInsert.setInt(3, cantidad);
                psInsert.executeUpdate();
            }

            actualizarImporte(con, idPedido);
            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la cantidad de unidades de una línea del carrito.
     * <p>
     * Tras modificar la cantidad recalcula el importe total del pedido
     * al que pertenece la línea mediante {@link #actualizarImporte}.
     * Se usa desde el carrito con los botones Ajax de + y -.
     * </p>
     *
     * @param idLinea  identificador de la línea a modificar
     * @param cantidad nueva cantidad de unidades (mínimo 1)
     * @return {@code true} si la actualización fue correcta, {@code false} si hubo error
     */
    public boolean actualizarCantidad(int idLinea, int cantidad) {

        String sql = "UPDATE lineaspedidos SET Cantidad = ? WHERE IdLinea = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            ps.setInt(1, cantidad);
            ps.setInt(2, idLinea);
            ps.executeUpdate();

            // Obtenemos el idPedido para recalcular importe
            int idPedido = obtenerIdPedidoDeLinea(con, idLinea);
            actualizarImporte(con, idPedido);

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una línea concreta del carrito.
     * <p>
     * Antes de borrar la línea obtiene el identificador del pedido
     * para poder recalcular el importe total tras la eliminación.
     * </p>
     *
     * @param idLinea identificador de la línea a eliminar
     * @return {@code true} si la eliminación fue correcta, {@code false} si hubo error
     */
    public boolean eliminarLinea(int idLinea) {

        String sql = "DELETE FROM lineaspedidos WHERE IdLinea = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            int idPedido = obtenerIdPedidoDeLinea(con, idLinea);
            ps.setInt(1, idLinea);
            ps.executeUpdate();

            actualizarImporte(con, idPedido);
            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vacía completamente el carrito eliminando sus líneas y el propio pedido.
     * <p>
     * Primero borra todas las líneas de {@code lineaspedidos} y después
     * elimina el registro del pedido de {@code pedidos}.
     * Ambas operaciones se ejecutan en la misma transacción.
     * </p>
     *
     * @param idPedido identificador del pedido a vaciar y eliminar
     * @return {@code true} si la operación fue correcta, {@code false} si hubo error
     */
    public boolean vaciarCarrito(int idPedido) {

        String sqlLineas = "DELETE FROM lineaspedidos WHERE IdPedido = ?";
        String sqlPedido = "DELETE FROM pedidos WHERE IdPedido = ?";

        try (Connection con = Conexion.getConexion()) {

            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(sqlLineas);
            ps1.setInt(1, idPedido);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(sqlPedido);
            ps2.setInt(1, idPedido);
            ps2.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de un pedido de carrito ({@code 'c'}) a finalizado ({@code 'f'}).
     * <p>
     * Actualiza también la fecha del pedido con la fecha actual del sistema,
     * que pasa a ser la fecha oficial de compra.
     * </p>
     *
     * @param idPedido identificador del pedido a finalizar
     * @return {@code true} si la operación fue correcta, {@code false} si hubo error
     */
    public boolean finalizarPedido(int idPedido) {

        String sql = "UPDATE pedidos SET Estado = 'f', Fecha = ? WHERE IdPedido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idPedido);
            ps.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // =====================================================================

    /**
     * Carga las líneas de detalle de un pedido reutilizando una conexión activa.
     * <p>
     * Realiza un JOIN entre {@code lineaspedidos}, {@code productos} y
     * {@code categorias} para construir objetos completos sin consultas adicionales.
     * Se llama siempre dentro de un bloque que ya tiene conexión abierta.
     * </p>
     *
     * @param con      conexión activa a la base de datos
     * @param idPedido identificador del pedido cuyas líneas se quieren cargar
     * @return lista de líneas con sus productos y categorías completos
     * @throws SQLException si ocurre un error al ejecutar la consulta
     */
    private List<LineaPedido> obtenerLineas(Connection con, int idPedido) throws SQLException {

        String sql = """
                    SELECT l.IdLinea, l.IdPedido, l.Cantidad,
                           p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                           c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                    FROM lineaspedidos l
                    JOIN productos p ON l.IdProducto = p.IdProducto
                    JOIN categorias c ON p.IdCategoria = c.IdCategoria
                    WHERE l.IdPedido = ?
                """;

        List<LineaPedido> lineas = new ArrayList<>();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idPedido);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Categoria categoria = new Categoria(
                    rs.getInt("IdCategoria"),
                    rs.getString("NombreCategoria"),
                    rs.getString("ImagenCategoria"));

            Producto producto = new Producto(
                    rs.getInt("IdProducto"),
                    categoria,
                    rs.getString("Nombre"),
                    rs.getString("Descripcion"),
                    rs.getDouble("Precio"),
                    rs.getString("Marca"),
                    rs.getString("Imagen"));

            lineas.add(new LineaPedido(
                    rs.getInt("IdLinea"),
                    rs.getInt("IdPedido"),
                    producto,
                    rs.getInt("Cantidad")));
        }

        return lineas;
    }

    /**
     * Recalcula y actualiza el importe e IVA de un pedido en la base de datos.
     * <p>
     * El importe se calcula como la suma de {@code Cantidad * Precio} de todas
     * las líneas del pedido. El IVA se calcula aplicando el 21% sobre ese importe.
     * Se usa {@code COALESCE} para devolver 0 si el carrito está vacío.
     * Se llama siempre después de cualquier operación que modifique las líneas.
     * </p>
     *
     * @param con      conexión activa a la base de datos
     * @param idPedido identificador del pedido a recalcular
     * @throws SQLException si ocurre un error al ejecutar la actualización
     */
    private void actualizarImporte(Connection con, int idPedido) throws SQLException {

        String sql = """
                    UPDATE pedidos
                    SET Importe = (
                        SELECT COALESCE(SUM(l.Cantidad * p.Precio), 0)
                        FROM lineaspedidos l
                        JOIN productos p ON l.IdProducto = p.IdProducto
                        WHERE l.IdPedido = ?
                    ),
                    Iva = (
                        SELECT COALESCE(SUM(l.Cantidad * p.Precio), 0) * 0.21
                        FROM lineaspedidos l
                        JOIN productos p ON l.IdProducto = p.IdProducto
                        WHERE l.IdPedido = ?
                    )
                    WHERE IdPedido = ?
                """;

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idPedido);
        ps.setInt(2, idPedido);
        ps.setInt(3, idPedido);
        ps.executeUpdate();
    }

    /**
     * Obtiene el identificador del pedido al que pertenece una línea.
     * <p>
     * Método auxiliar usado antes de eliminar o actualizar una línea,
     * para poder recalcular el importe del pedido afectado.
     * </p>
     *
     * @param con     conexión activa a la base de datos
     * @param idLinea identificador de la línea
     * @return identificador del pedido, o {@code -1} si la línea no existe
     * @throws SQLException si ocurre un error al ejecutar la consulta
     */
    private int obtenerIdPedidoDeLinea(Connection con, int idLinea) throws SQLException {

        String sql = "SELECT IdPedido FROM lineaspedidos WHERE IdLinea = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idLinea);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("IdPedido");
        }

        return -1;
    }

    /**
     * Alias de {@link #añadirLinea(int, int, int)} para compatibilidad
     * con llamadas que no usan caracteres especiales en el nombre del método.
     *
     * @param idPedido   identificador del pedido (carrito)
     * @param idProducto identificador del producto a añadir
     * @param cantidad   número de unidades a añadir
     * @return {@code true} si la operación fue correcta, {@code false} si hubo error
     */
    public boolean anadirLinea(int idPedido, int idProducto, int cantidad) {
        return añadirLinea(idPedido, idProducto, cantidad);
    }
}
