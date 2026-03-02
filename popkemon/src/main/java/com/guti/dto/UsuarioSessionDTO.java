package com.guti.dto;

public class UsuarioSessionDTO {

    private int idUsuario;
    private String email;
    private String nombre;
    private String apellidos;
    private String avatar;

    public UsuarioSessionDTO() {}

    public UsuarioSessionDTO(int idUsuario, String email, String nombre,
                              String apellidos, String avatar) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.avatar = avatar;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    @Override
    public String toString() {
        return "UsuarioSessionDTO{" +
                "idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
