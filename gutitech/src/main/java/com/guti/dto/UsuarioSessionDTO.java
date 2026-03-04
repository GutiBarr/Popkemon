package com.guti.dto;

/**
 * DTO (Data Transfer Object) que representa los datos del usuario en sesión.
 * <p>
 * Contiene únicamente los campos necesarios para gestionar la sesión activa
 * y mostrar información del usuario en la interfaz (nombre, avatar, etc.).
 * Se almacena en la sesión HTTP bajo el atributo {@code usuarioSession}
 * tras un login correcto, evitando guardar datos sensibles como la contraseña.
 * </p>
 * <p>
 * Al usar este DTO en lugar del bean {@link com.guti.beans.Usuario} completo,
 * se reduce la información expuesta en sesión y el consumo de memoria.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.service.AuthService
 * @see com.guti.beans.Usuario
 */
public class UsuarioSessionDTO {

    /** Identificador único del usuario autenticado. */
    private int idUsuario;

    /** Correo electrónico del usuario autenticado. */
    private String email;

    /** Nombre de pila del usuario autenticado. */
    private String nombre;

    /** Apellidos del usuario autenticado. */
    private String apellidos;

    /**
     * Nombre del archivo de avatar del usuario.
     * Se usa para mostrar la imagen de perfil en la cabecera de la aplicación.
     */
    private String avatar;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public UsuarioSessionDTO() {}

    /**
     * Constructor con todos los campos.
     *
     * @param idUsuario identificador único del usuario
     * @param email     correo electrónico del usuario
     * @param nombre    nombre de pila del usuario
     * @param apellidos apellidos del usuario
     * @param avatar    nombre del archivo de avatar
     */
    public UsuarioSessionDTO(int idUsuario, String email, String nombre,
                              String apellidos, String avatar) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.avatar = avatar;
    }

    /**
     * Devuelve el identificador único del usuario.
     *
     * @return identificador del usuario
     */
    public int getIdUsuario() { return idUsuario; }

    /**
     * Establece el identificador único del usuario.
     *
     * @param idUsuario identificador del usuario
     */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Devuelve el correo electrónico del usuario.
     *
     * @return email del usuario
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email email del usuario
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Devuelve el nombre de pila del usuario.
     *
     * @return nombre del usuario
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre de pila del usuario.
     *
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Devuelve los apellidos del usuario.
     *
     * @return apellidos del usuario
     */
    public String getApellidos() { return apellidos; }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos apellidos del usuario
     */
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    /**
     * Devuelve el nombre del archivo de avatar del usuario.
     *
     * @return nombre del archivo de avatar
     */
    public String getAvatar() { return avatar; }

    /**
     * Establece el nombre del archivo de avatar del usuario.
     *
     * @param avatar nombre del archivo de avatar
     */
    public void setAvatar(String avatar) { this.avatar = avatar; }

    /**
     * Devuelve una representación textual del objeto {@code UsuarioSessionDTO}.
     *
     * @return cadena con el identificador, email y nombre del usuario
     */
    @Override
    public String toString() {
        return "UsuarioSessionDTO{" +
                "idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}