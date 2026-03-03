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

public class PedidoDAO {

    /*
     * =====================================
     * OBTENER CARRITO ACTIVO DEL USUARIO
     * =====================================
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

    /*
     * =====================================
     * OBTENER PEDIDOS FINALIZADOS DEL USUARIO
     * =====================================
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

    /*
     * =====================================
     * CREAR CARRITO NUEVO
     * =====================================
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

    /*
     * =====================================
     * AÑADIR PRODUCTO AL CARRITO
     * =====================================
     */
    public boolean añadirLinea(int idPedido, int idProducto, int cantidad) {

        // Si ya existe la línea, sumamos cantidad
        String sqlCheck = "SELECT IdLinea, Cantidad FROM lineaspedidos WHERE IdPedido = ? AND IdProducto = ?";
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

    /*
     * =====================================
     * ACTUALIZAR CANTIDAD DE UNA LINEA
     * =====================================
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

    /*
     * =====================================
     * ELIMINAR LINEA DEL CARRITO
     * =====================================
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

    /*
     * =====================================
     * VACIAR CARRITO ENTERO
     * =====================================
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

    /*
     * =====================================
     * FINALIZAR PEDIDO
     * =====================================
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

    /*
     * =====================================
     * MÉTODOS PRIVADOS AUXILIARES
     * =====================================
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

    public void anadirLinea(int idPedido, int idProducto, int cantidad) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
