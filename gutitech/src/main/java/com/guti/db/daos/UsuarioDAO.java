package com.guti.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import com.guti.beans.Usuario;
import com.guti.db.Conexion;

/**
 * DAO para la gestión de usuarios registrados en la base de datos.
 * <p>
 * Proporciona operaciones de consulta, inserción y actualización sobre
 * la tabla {@code usuarios}. Las contraseñas nunca se manipulan en texto
 * plano; el hashing y la verificación se delegan en
 * {@link com.guti.service.AuthService}.
 * </p>
 * <p>
 * Todas las operaciones de escritura usan transacciones explícitas
 * con {@code setAutoCommit(false)} para garantizar la integridad de los datos.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.beans.Usuario
 * @see com.guti.service.AuthService
 * @see com.guti.db.Conexion
 */
public class UsuarioDAO {

    /**
     * Recupera un usuario a partir de su dirección de correo electrónico.
     * <p>
     * Se utiliza principalmente durante el proceso de login para obtener
     * el usuario y verificar su contraseña con Argon2id.
     * Convierte el {@code Timestamp} de {@code UltimoAcceso} a
     * {@code LocalDateTime} si no es nulo.
     * </p>
     *
     * @param email correo electrónico del usuario a buscar
     * @return el usuario con todos sus datos, o {@code null} si no existe
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

    /**
     * Recupera un usuario a partir de su identificador único.
     * <p>
     * Se utiliza para recargar los datos del usuario desde la base de datos,
     * por ejemplo al actualizar el perfil y refrescar la sesión.
     * </p>
     *
     * @param id identificador único del usuario a buscar
     * @return el usuario con todos sus datos, o {@code null} si no existe
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

    /**
     * Comprueba si un correo electrónico ya está registrado en la base de datos.
     * <p>
     * Se utiliza en el formulario de registro mediante una petición Ajax
     * para validar en tiempo real que el email no esté duplicado antes
     * de enviar el formulario completo.
     * Usa {@code SELECT 1} para mayor eficiencia, evitando traer datos innecesarios.
     * </p>
     *
     * @param email correo electrónico a comprobar
     * @return {@code true} si el email ya existe, {@code false} si está disponible
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

    /**
     * Inserta un nuevo usuario en la base de datos.
     * <p>
     * La contraseña almacenada en el objeto {@code Usuario} debe ser
     * ya el hash Argon2id generado por {@link com.guti.service.AuthService},
     * nunca la contraseña en texto plano.
     * Tras la inserción, actualiza el {@code idUsuario} del objeto
     * con la clave generada automáticamente por la BD.
     * </p>
     *
     * @param u objeto con los datos del usuario a insertar
     * @return {@code true} si la inserción fue correcta, {@code false} si hubo error
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

    /**
     * Actualiza los datos personales y de contacto de un usuario existente.
     * <p>
     * Actualiza nombre, apellidos, teléfono, dirección, código postal,
     * localidad, provincia y avatar. No modifica el email ni la contraseña.
     * Para cambiar la contraseña usar {@link #actualizarPassword(int, String)}.
     * </p>
     *
     * @param u objeto con los datos actualizados del usuario
     * @return {@code true} si la actualización fue correcta, {@code false} si hubo error
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

    /**
     * Actualiza la contraseña de un usuario en la base de datos.
     * <p>
     * El parámetro {@code nuevaPassword} debe ser el hash Argon2id
     * de la nueva contraseña, generado previamente por
     * {@link com.guti.security.PasswordHasher#hashPassword(char[])}.
     * Nunca se almacena la contraseña en texto plano.
     * </p>
     *
     * @param idUsuario     identificador del usuario cuya contraseña se actualiza
     * @param nuevaPassword hash Argon2id de la nueva contraseña
     * @return {@code true} si la actualización fue correcta, {@code false} si hubo error
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

    /**
     * Actualiza el campo {@code UltimoAcceso} de un usuario con la fecha y hora actuales.
     * <p>
     * Se invoca automáticamente al finalizar el proceso de login correcto,
     * permitiendo registrar cuándo accedió el usuario por última vez.
     * Esta operación no usa transacción explícita al ser una escritura
     * de baja criticidad.
     * </p>
     *
     * @param idUsuario identificador del usuario a actualizar
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