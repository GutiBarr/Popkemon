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

/**
 * DAO para la gestión de productos en la base de datos.
 * <p>
 * Proporciona métodos para listar, buscar y filtrar los productos
 * de la tienda almacenados en la tabla {@code productos}.
 * Todas las consultas realizan un JOIN con {@code categorias} para
 * devolver objetos {@link Producto} con su {@link Categoria} completa.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.beans.Producto
 * @see com.guti.beans.Categoria
 * @see com.guti.db.Conexion
 */
public class ProductoDAO {

    /**
     * Recupera todos los productos de la base de datos ordenados por identificador.
     * <p>
     * Realiza un JOIN con {@code categorias} para construir objetos
     * {@link Producto} completos con su categoría asociada.
     * </p>
     *
     * @return lista completa de productos disponibles,
     *         o lista vacía si no hay ninguno o se produce un error
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

    /**
     * Recupera un producto concreto por su identificador.
     * <p>
     * Usa {@code PreparedStatement} para evitar inyección SQL.
     * Devuelve {@code null} si no existe ningún producto con ese identificador.
     * </p>
     *
     * @param id identificador único del producto a buscar
     * @return el producto encontrado con su categoría completa,
     *         o {@code null} si no existe
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

    /**
     * Recupera todos los productos de una categoría concreta.
     * <p>
     * Filtra por {@code IdCategoria} y devuelve los resultados
     * ordenados por identificador de producto.
     * </p>
     *
     * @param idCategoria identificador de la categoría a filtrar
     * @return lista de productos pertenecientes a la categoría indicada,
     *         o lista vacía si no hay ninguno
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

    /**
     * Busca productos cuyo nombre o descripción contengan el texto indicado.
     * <p>
     * La búsqueda es insensible a mayúsculas/minúsculas y usa {@code LIKE}
     * con comodines {@code %texto%} en ambos extremos.
     * </p>
     *
     * @param texto texto a buscar en el nombre o descripción del producto
     * @return lista de productos que coinciden con la búsqueda,
     *         o lista vacía si no hay resultados
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

    /**
     * Busca productos aplicando múltiples filtros combinados.
     * <p>
     * Todos los parámetros son opcionales. Si se pasa {@code null} o vacío,
     * ese filtro no se aplica. La consulta SQL se construye dinámicamente
     * con {@code WHERE 1=1} añadiendo condiciones según los parámetros recibidos,
     * lo que permite cualquier combinación de filtros sin duplicar código.
     * </p>
     * <p>
     * Valores válidos para el parámetro {@code orden}:
     * </p>
     * <ul>
     *   <li>{@code "precioAsc"}  — ordena por precio ascendente</li>
     *   <li>{@code "precioDesc"} — ordena por precio descendente</li>
     *   <li>{@code "nombre"}     — ordena alfabéticamente por nombre</li>
     *   <li>{@code null} o cualquier otro valor — ordena por identificador</li>
     * </ul>
     *
     * @param busqueda    texto a buscar en nombre o descripción (null = sin filtro)
     * @param idCategoria identificador de categoría (null = todas las categorías)
     * @param marca       marca exacta a filtrar (null o vacío = todas las marcas)
     * @param precioMin   precio mínimo del rango (null = sin límite inferior)
     * @param precioMax   precio máximo del rango (null = sin límite superior)
     * @param orden       criterio de ordenación (null = orden por defecto)
     * @return lista de productos que cumplen todos los filtros indicados,
     *         o lista vacía si no hay resultados
     */
    public List<Producto> buscarConFiltros(String busqueda, Integer idCategoria,
            String marca, Double precioMin, Double precioMax, String orden) {

        StringBuilder sql = new StringBuilder("""
                SELECT p.IdProducto, p.Nombre, p.Descripcion, p.Precio, p.Marca, p.Imagen,
                       c.IdCategoria, c.Nombre AS NombreCategoria, c.Imagen AS ImagenCategoria
                FROM productos p
                JOIN categorias c ON p.IdCategoria = c.IdCategoria
                WHERE 1=1
                """);

        if (busqueda != null && !busqueda.isEmpty()) {
            sql.append(" AND (p.Nombre LIKE ? OR p.Descripcion LIKE ?)");
        }
        if (idCategoria != null) {
            sql.append(" AND p.IdCategoria = ?");
        }
        if (marca != null && !marca.isEmpty()) {
            sql.append(" AND p.Marca = ?");
        }
        if (precioMin != null) {
            sql.append(" AND p.Precio >= ?");
        }
        if (precioMax != null) {
            sql.append(" AND p.Precio <= ?");
        }

        if ("precioAsc".equals(orden)) {
            sql.append(" ORDER BY p.Precio ASC");
        } else if ("precioDesc".equals(orden)) {
            sql.append(" ORDER BY p.Precio DESC");
        } else if ("nombre".equals(orden)) {
            sql.append(" ORDER BY p.Nombre ASC");
        } else {
            sql.append(" ORDER BY p.IdProducto");
        }

        List<Producto> productos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;
            if (busqueda != null && !busqueda.isEmpty()) {
                ps.setString(i++, "%" + busqueda + "%");
                ps.setString(i++, "%" + busqueda + "%");
            }
            if (idCategoria != null) {
                ps.setInt(i++, idCategoria);
            }
            if (marca != null && !marca.isEmpty()) {
                ps.setString(i++, marca);
            }
            if (precioMin != null) {
                ps.setDouble(i++, precioMin);
            }
            if (precioMax != null) {
                ps.setDouble(i++, precioMax);
            }

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

    /**
     * Recupera la lista de marcas distintas existentes en la base de datos.
     * <p>
     * Usa {@code SELECT DISTINCT} para evitar duplicados y devuelve
     * los resultados ordenados alfabéticamente. Se utiliza para poblar
     * el selector de marcas del formulario de filtros de la tienda.
     * </p>
     *
     * @return lista de nombres de marca ordenados alfabéticamente,
     *         o lista vacía si no hay productos en la BD
     */
    public List<String> listarMarcas() {

        String sql = "SELECT DISTINCT Marca FROM productos ORDER BY Marca";
        List<String> marcas = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             java.sql.Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                marcas.add(rs.getString("Marca"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marcas;
    }
}
