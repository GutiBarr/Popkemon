package com.guti.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.guti.beans.Categoria;
import com.guti.db.Conexion;

public class CategoriaDAO {

    /*
     * =====================================
     * SELECT ALL
     * =====================================
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

    /*
     * =====================================
     * SELECT POR ID
     * =====================================
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