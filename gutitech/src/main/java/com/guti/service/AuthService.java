package com.guti.service;

import com.guti.beans.Usuario;
import com.guti.db.daos.UsuarioDAO;
import com.guti.dto.UsuarioSessionDTO;
import com.guti.security.PasswordHasher;

/**
 * Servicio de autenticación y registro de usuarios.
 * <p>
 * Centraliza la lógica de negocio relacionada con la seguridad:
 * verificación de credenciales en el login y hashing de contraseña
 * en el registro. Delega el acceso a datos en {@link UsuarioDAO}
 * y el hashing en {@link PasswordHasher}.
 * </p>
 * <p>
 * Todos los métodos son estáticos, por lo que no es necesario instanciar
 * la clase para usarla.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.security.PasswordHasher
 * @see com.guti.db.daos.UsuarioDAO
 * @see com.guti.dto.UsuarioSessionDTO
 */
public class AuthService {

    /**
     * Autentica un usuario con email y contraseña.
     * <p>
     * Busca el usuario por email en la base de datos y verifica la contraseña
     * introducida contra el hash Argon2id almacenado mediante
     * {@link PasswordHasher#verifyPassword}. Si las credenciales son correctas,
     * actualiza el campo {@code UltimoAcceso} del usuario y devuelve un
     * {@link UsuarioSessionDTO} para almacenar en la sesión HTTP.
     * </p>
     * <p>
     * Se usa el mismo mensaje de error tanto si el email no existe como si
     * la contraseña es incorrecta, para no revelar qué dato falla
     * (protección contra enumeración de usuarios).
     * </p>
     *
     * @param email    correo electrónico introducido por el usuario
     * @param password contraseña en texto plano introducida por el usuario
     * @return {@link UsuarioSessionDTO} con los datos de sesión del usuario autenticado
     * @throws Exception si el email no existe o la contraseña es incorrecta,
     *                   con el mensaje {@code "Email o contraseña incorrectos"}
     */
    public static UsuarioSessionDTO login(String email, String password) throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.obtenerPorEmail(email);

        if (usuario == null) {
            throw new Exception("Email o contraseña incorrectos");
        }

        if (!PasswordHasher.verifyPassword(usuario.getPassword(), password.toCharArray())) {
            throw new Exception("Email o contraseña incorrectos");
        }

        usuarioDAO.actualizarUltimoAcceso(usuario.getIdUsuario());

        return new UsuarioSessionDTO(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getAvatar());
    }

    /**
     * Registra un nuevo usuario en la aplicación.
     * <p>
     * Comprueba primero si el email ya está registrado mediante
     * {@link UsuarioDAO#emailExiste}. Si existe, devuelve {@code false}
     * sin realizar ninguna inserción.
     * Si el email está disponible, hashea la contraseña en texto plano
     * del objeto {@code Usuario} con Argon2id y la sustituye antes
     * de insertar el registro en la base de datos.
     * </p>
     *
     * @param usuario objeto con los datos del nuevo usuario.
     *                El campo {@code password} debe contener la contraseña
     *                en texto plano; este método la reemplaza por el hash.
     * @return {@code true} si el registro fue correcto,
     *         {@code false} si el email ya estaba registrado o hubo un error
     */
    public static boolean registrar(Usuario usuario) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (usuarioDAO.emailExiste(usuario.getEmail())) {
            return false;
        }

        String hash = PasswordHasher.hashPassword(usuario.getPassword().toCharArray());
        usuario.setPassword(hash);

        boolean resultado = usuarioDAO.insertar(usuario);
        return resultado;
    }
}
