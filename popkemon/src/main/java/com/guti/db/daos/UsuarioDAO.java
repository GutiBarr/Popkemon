package com.guti.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import com.guti.beans.Usuario;
import com.guti.db.Conexion;

public class UsuarioDAO {

    /*
     * =====================================
     * SELECT POR EMAIL
     * =====================================
     */
    public Usuario obtenerPorEmail(String email) {

        String sql = """
                    SELECT IdUsuario, Email, Password, Nombre, Apellidos, NIF,
                           Telefono, Direccion, CodigoPostal, Localidad, Provincia,
                           UltimoAcceso, Avatar
                    FROM usuarios
                    WHERE Email = ?
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("IdUsuario"));
                u.setEmail(rs.getString("Email"));
                u.setPassword(rs.getString("Password"));
                u.setNombre(rs.getString("Nombre"));
                u.setApellidos(rs.getString("Apellidos"));
                u.setNif(rs.getString("NIF"));
                u.setTelefono(rs.getString("Telefono"));
                u.setDireccion(rs.getString("Direccion"));
                u.setCodigoPostal(rs.getString("CodigoPostal"));
                u.setLocalidad(rs.getString("Localidad"));
                u.setProvincia(rs.getString("Provincia"));
                if (rs.getTimestamp("UltimoAcceso") != null) {
                    u.setUltimoAcceso(rs.getTimestamp("UltimoAcceso").toLocalDateTime());
                }
                u.setAvatar(rs.getString("Avatar"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * =====================================
     * SELECT POR ID
     * =====================================
     */
    public Usuario obtenerPorId(int id) {

        String sql = """
                    SELECT IdUsuario, Email, Password, Nombre, Apellidos, NIF,
                           Telefono, Direccion, CodigoPostal, Localidad, Provincia,
                           UltimoAcceso, Avatar
                    FROM usuarios
                    WHERE IdUsuario = ?
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("IdUsuario"));
                u.setEmail(rs.getString("Email"));
                u.setPassword(rs.getString("Password"));
                u.setNombre(rs.getString("Nombre"));
                u.setApellidos(rs.getString("Apellidos"));
                u.setNif(rs.getString("NIF"));
                u.setTelefono(rs.getString("Telefono"));
                u.setDireccion(rs.getString("Direccion"));
                u.setCodigoPostal(rs.getString("CodigoPostal"));
                u.setLocalidad(rs.getString("Localidad"));
                u.setProvincia(rs.getString("Provincia"));
                if (rs.getTimestamp("UltimoAcceso") != null) {
                    u.setUltimoAcceso(rs.getTimestamp("UltimoAcceso").toLocalDateTime());
                }
                u.setAvatar(rs.getString("Avatar"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * =====================================
     * EMAIL YA EXISTE (para Ajax)
     * =====================================
     */
    public boolean emailExiste(String email) {

        String sql = "SELECT 1 FROM usuarios WHERE Email = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * =====================================
     * INSERT
     * =====================================
     */
    public boolean insertar(Usuario u) {

        String sql = """
                    INSERT INTO usuarios (Email, Password, Nombre, Apellidos, NIF,
                                         Telefono, Direccion, CodigoPostal, Localidad,
                                         Provincia, Avatar)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            con.setAutoCommit(false);

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellidos());
            ps.setString(5, u.getNif());
            ps.setString(6, u.getTelefono());
            ps.setString(7, u.getDireccion());
            ps.setString(8, u.getCodigoPostal());
            ps.setString(9, u.getLocalidad());
            ps.setString(10, u.getProvincia());
            ps.setString(11, u.getAvatar());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                u.setIdUsuario(rs.getInt(1));
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * =====================================
     * UPDATE PERFIL
     * =====================================
     */
    public boolean actualizarPerfil(Usuario u) {

        String sql = """
                    UPDATE usuarios
                    SET Nombre = ?, Apellidos = ?, Telefono = ?, Direccion = ?,
                        CodigoPostal = ?, Localidad = ?, Provincia = ?, Avatar = ?
                    WHERE IdUsuario = ?
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getDireccion());
            ps.setString(5, u.getCodigoPostal());
            ps.setString(6, u.getLocalidad());
            ps.setString(7, u.getProvincia());
            ps.setString(8, u.getAvatar());
            ps.setInt(9, u.getIdUsuario());
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
     * UPDATE PASSWORD
     * =====================================
     */
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {

        String sql = "UPDATE usuarios SET Password = ? WHERE IdUsuario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            ps.setString(1, nuevaPassword);
            ps.setInt(2, idUsuario);
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
     * UPDATE ULTIMO ACCESO
     * =====================================
     */
    public void actualizarUltimoAcceso(int idUsuario) {

        String sql = "UPDATE usuarios SET UltimoAcceso = ? WHERE IdUsuario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idUsuario);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
