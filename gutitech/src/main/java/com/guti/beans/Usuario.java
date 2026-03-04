package com.guti.beans;

import java.time.LocalDateTime;

/**
 * Bean que representa un usuario registrado en la tienda.
 * <p>
 * Almacena todos los datos personales, de contacto y de acceso del usuario.
 * La contraseña se guarda siempre como hash generado con el algoritmo
 * <strong>Argon2id</strong>, nunca en texto plano.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.service.AuthService
 * @see com.guti.db.daos.UsuarioDAO
 */
public class Usuario {

    /** Identificador único del usuario (clave primaria en BD). */
    private int idUsuario;

    /** Correo electrónico del usuario. Valor único en la BD. */
    private String email;

    /**
     * Contraseña del usuario almacenada como hash Argon2id.
     * Nunca se almacena ni se transmite en texto plano.
     */
    private String password;

    /** Nombre de pila del usuario. */
    private String nombre;

    /** Apellidos del usuario. */
    private String apellidos;

    /** Número de identificación fiscal (DNI/NIE) del usuario. */
    private String nif;

    /** Teléfono de contacto del usuario. */
    private String telefono;

    /** Dirección postal del usuario. */
    private String direccion;

    /** Código postal del domicilio del usuario. */
    private String codigoPostal;

    /** Localidad del domicilio del usuario. */
    private String localidad;

    /** Provincia del domicilio del usuario. */
    private String provincia;

    /** Fecha y hora del último acceso del usuario a la aplicación. */
    private LocalDateTime ultimoAcceso;

    /**
     * Nombre del archivo de avatar del usuario.
     * El archivo se sirve a través de {@code MediaServlet}
     * desde el directorio de uploads del servidor.
     */
    private String avatar;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Usuario() {}

    /**
     * Constructor con todos los campos.
     *
     * @param idUsuario    identificador único del usuario
     * @param email        correo electrónico (único en BD)
     * @param password     hash Argon2id de la contraseña
     * @param nombre       nombre de pila del usuario
     * @param apellidos    apellidos del usuario
     * @param nif          número de identificación fiscal
     * @param telefono     teléfono de contacto
     * @param direccion    dirección postal
     * @param codigoPostal código postal del domicilio
     * @param localidad    localidad del domicilio
     * @param provincia    provincia del domicilio
     * @param ultimoAcceso fecha y hora del último acceso
     * @param avatar       nombre del archivo de avatar
     */
    public Usuario(int idUsuario, String email, String password, String nombre,
                   String apellidos, String nif, String telefono, String direccion,
                   String codigoPostal, String localidad, String provincia,
                   LocalDateTime ultimoAcceso, String avatar) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nif = nif;
        this.telefono = telefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.ultimoAcceso = ultimoAcceso;
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
     * Devuelve el hash Argon2id de la contraseña del usuario.
     *
     * @return hash de la contraseña
     */
    public String getPassword() { return password; }

    /**
     * Establece el hash Argon2id de la contraseña del usuario.
     *
     * @param password hash de la contraseña
     */
    public void setPassword(String password) { this.password = password; }

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
     * Devuelve el número de identificación fiscal del usuario.
     *
     * @return NIF del usuario
     */
    public String getNif() { return nif; }

    /**
     * Establece el número de identificación fiscal del usuario.
     *
     * @param nif NIF del usuario
     */
    public void setNif(String nif) { this.nif = nif; }

    /**
     * Devuelve el teléfono de contacto del usuario.
     *
     * @return teléfono del usuario
     */
    public String getTelefono() { return telefono; }

    /**
     * Establece el teléfono de contacto del usuario.
     *
     * @param telefono teléfono del usuario
     */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Devuelve la dirección postal del usuario.
     *
     * @return dirección del usuario
     */
    public String getDireccion() { return direccion; }

    /**
     * Establece la dirección postal del usuario.
     *
     * @param direccion dirección del usuario
     */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Devuelve el código postal del domicilio del usuario.
     *
     * @return código postal
     */
    public String getCodigoPostal() { return codigoPostal; }

    /**
     * Establece el código postal del domicilio del usuario.
     *
     * @param codigoPostal código postal
     */
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    /**
     * Devuelve la localidad del domicilio del usuario.
     *
     * @return localidad del usuario
     */
    public String getLocalidad() { return localidad; }

    /**
     * Establece la localidad del domicilio del usuario.
     *
     * @param localidad localidad del usuario
     */
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    /**
     * Devuelve la provincia del domicilio del usuario.
     *
     * @return provincia del usuario
     */
    public String getProvincia() { return provincia; }

    /**
     * Establece la provincia del domicilio del usuario.
     *
     * @param provincia provincia del usuario
     */
    public void setProvincia(String provincia) { this.provincia = provincia; }

    /**
     * Devuelve la fecha y hora del último acceso del usuario a la aplicación.
     *
     * @return fecha y hora del último acceso
     */
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }

    /**
     * Establece la fecha y hora del último acceso del usuario a la aplicación.
     *
     * @param ultimoAcceso fecha y hora del último acceso
     */
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

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
     * Devuelve una representación textual del objeto {@code Usuario}.
     *
     * @return cadena con el identificador, email, nombre y apellidos del usuario
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                '}';
    }
}
