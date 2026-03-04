package com.guti.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.guti.beans.Categoria;
import com.guti.db.Conexion;

/**
 * DAO (Data Access Object) para la gestión de categorías en la base de datos.
 * <p>
 * Proporciona métodos para consultar las categorías de productos almacenadas
 * en la tabla {@code categorias} de la base de datos {@code tienda_on_line}.
 * Utiliza {@link com.guti.db.Conexion} para obtener las conexiones a la BD.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.beans.Categoria
 * @see com.guti.db.Conexion
 */
public class CategoriaDAO {

    /**
     * Recupera todas las categorías de la base de datos ordenadas por identificador.
     * <p>
     * Ejecuta un {@code SELECT} sobre la tabla {@code categorias} y construye
     * un objeto {@link Categoria} por cada fila del resultado.
     * </p>
     *
     * @return lista con todas las categorías disponibles,
     *         o lista vacía si no hay ninguna o se produce un error
     */
    public List<Categoria> listarCategorias() {

        String sql = "SELECT IdCategoria, Nombre, Imagen FROM categorias ORDER BY IdCategoria";

        List<Categoria> categorias = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("Nombre"),
                        rs.getString("Imagen")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }

    /**
     * Recupera una categoría concreta por su identificador.
     * <p>
     * Ejecuta un {@code SELECT} con {@code PreparedStatement} para evitar
     * inyección SQL. Devuelve {@code null} si no existe ninguna categoría
     * con el identificador indicado.
     * </p>
     *
     * @param id identificador único de la categoría a buscar
     * @return la categoría encontrada, o {@code null} si no existe
     */
    public Categoria obtenerPorId(int id) {

        String sql = "SELECT IdCategoria, Nombre, Imagen FROM categorias WHERE IdCategoria = ?";

        try (Connection con = Conexion.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        rs.getInt("IdCategoria"),
                        rs.getString("Nombre"),
                        rs.getString("Imagen"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}