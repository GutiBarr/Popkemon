package com.guti.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.guti.beans.Categoria;
import com.guti.beans.Producto;
import com.guti.db.Conexion;

public class ProductoDAO {

    /*
     * =====================================
     * SELECT ALL
     * =====================================
     */
    public List<Producto> listarProductos() {

        String sql = """
                    SELECT p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                           c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                    FROM productos p
                    JOIN categorias c ON p.IdCategoria = c.IdCategoria
                    ORDER BY p.IdProducto
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("NombreCategoria"),
                        rs.getString("ImagenCategoria"));

                productos.add(new Producto(
                        rs.getInt("IdProducto"),
                        categoria,
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio"),
                        rs.getString("Marca"),
                        rs.getString("Imagen")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    /*
     * =====================================
     * SELECT POR ID
     * =====================================
     */
    public Producto obtenerPorId(int id) {

        String sql = """
                    SELECT p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                           c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                    FROM productos p
                    JOIN categorias c ON p.IdCategoria = c.IdCategoria
                    WHERE p.IdProducto = ?
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("NombreCategoria"),
                        rs.getString("ImagenCategoria"));

                return new Producto(
                        rs.getInt("IdProducto"),
                        categoria,
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio"),
                        rs.getString("Marca"),
                        rs.getString("Imagen"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * =====================================
     * SELECT POR CATEGORIA
     * =====================================
     */
    public List<Producto> listarPorCategoria(int idCategoria) {

        String sql = """
                    SELECT p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                           c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                    FROM productos p
                    JOIN categorias c ON p.IdCategoria = c.IdCategoria
                    WHERE p.IdCategoria = ?
                    ORDER BY p.IdProducto
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("NombreCategoria"),
                        rs.getString("ImagenCategoria"));

                productos.add(new Producto(
                        rs.getInt("IdProducto"),
                        categoria,
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio"),
                        rs.getString("Marca"),
                        rs.getString("Imagen")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    /*
     * =====================================
     * BUSCAR POR NOMBRE
     * =====================================
     */
    public List<Producto> buscarPorNombre(String texto) {

        String sql = """
                    SELECT p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                           c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                    FROM productos p
                    JOIN categorias c ON p.IdCategoria = c.IdCategoria
                    WHERE p.Nombre LIKE ? OR p.Descripcion LIKE ?
                    ORDER BY p.IdProducto
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("NombreCategoria"),
                        rs.getString("ImagenCategoria"));

                productos.add(new Producto(
                        rs.getInt("IdProducto"),
                        categoria,
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio"),
                        rs.getString("Marca"),
                        rs.getString("Imagen")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }
}
