package com.guti.service;

import com.guti.beans.Usuario;
import com.guti.db.daos.UsuarioDAO;
import com.guti.dto.UsuarioSessionDTO;
import com.guti.security.PasswordHasher;

public class AuthService {

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

    public static boolean registrar(Usuario usuario) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (usuarioDAO.emailExiste(usuario.getEmail())) {
            return false;
        }

        String hash = PasswordHasher.hashPassword(usuario.getPassword().toCharArray());
        usuario.setPassword(hash);

        return usuarioDAO.insertar(usuario);
    }
}
