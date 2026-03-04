package com.guti.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean que representa un pedido o carrito de compra de un usuario.
 * <p>
 * Un pedido puede estar en dos estados:
 * </p>
 * <ul>
 *   <li>{@code 'c'} - Carrito: pedido abierto, el usuario todavía puede modificarlo.</li>
 *   <li>{@code 'f'} - Finalizado: pedido confirmado y cerrado.</li>
 * </ul>
 * <p>
 * Cada pedido contiene una lista de {@link LineaPedido} con los productos incluidos.
 * La lista se inicializa vacía en todos los constructores.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see LineaPedido
 * @see com.guti.db.daos.PedidoDAO
 */
public class Pedido {

    /** Identificador único del pedido (clave primaria en BD). */
    private int idPedido;

    /** Fecha en la que se creó el pedido. */
    private LocalDate fecha;

    /**
     * Estado del pedido.
     * {@code 'c'} para carrito activo, {@code 'f'} para pedido finalizado.
     */
    private char estado;

    /** Identificador del usuario propietario del pedido (clave foránea). */
    private int idUsuario;

    /** Importe total del pedido sin IVA. */
    private double importe;

    /** Importe del IVA (21%) aplicado al pedido. */
    private double iva;

    /** Lista de líneas de detalle del pedido. Nunca es {@code null}. */
    private List<LineaPedido> lineas;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     * Inicializa la lista de líneas vacía.
     */
    public Pedido() {
        this.lineas = new ArrayList<>();
    }

    /**
     * Constructor con todos los campos principales.
     * La lista de líneas se inicializa vacía y se puede poblar
     * posteriormente con {@link #setLineas(List)}.
     *
     * @param idPedido  identificador único del pedido
     * @param fecha     fecha de creación del pedido
     * @param estado    estado del pedido ({@code 'c'} o {@code 'f'})
     * @param idUsuario identificador del usuario propietario
     * @param importe   importe total sin IVA
     * @param iva       importe del IVA aplicado
     */
    public Pedido(int idPedido, LocalDate fecha, char estado,
                  int idUsuario, double importe, double iva) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.importe = importe;
        this.iva = iva;
        this.lineas = new ArrayList<>();
    }

    /**
     * Devuelve el identificador único del pedido.
     *
     * @return identificador del pedido
     */
    public int getIdPedido() { return idPedido; }

    /**
     * Establece el identificador único del pedido.
     *
     * @param idPedido identificador del pedido
     */
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    /**
     * Devuelve la fecha de creación del pedido.
     *
     * @return fecha del pedido
     */
    public LocalDate getFecha() { return fecha; }

    /**
     * Establece la fecha de creación del pedido.
     *
     * @param fecha fecha del pedido
     */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    /**
     * Devuelve el estado del pedido.
     *
     * @return {@code 'c'} si es un carrito activo, {@code 'f'} si está finalizado
     */
    public char getEstado() { return estado; }

    /**
     * Establece el estado del pedido.
     *
     * @param estado {@code 'c'} para carrito activo, {@code 'f'} para finalizado
     */
    public void setEstado(char estado) { this.estado = estado; }

    /**
     * Devuelve el identificador del usuario propietario del pedido.
     *
     * @return identificador del usuario
     */
    public int getIdUsuario() { return idUsuario; }

    /**
     * Establece el identificador del usuario propietario del pedido.
     *
     * @param idUsuario identificador del usuario
     */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Devuelve el importe total del pedido sin IVA.
     *
     * @return importe sin IVA
     */
    public double getImporte() { return importe; }

    /**
     * Establece el importe total del pedido sin IVA.
     *
     * @param importe importe sin IVA
     */
    public void setImporte(double importe) { this.importe = importe; }

    /**
     * Devuelve el importe del IVA aplicado al pedido.
     *
     * @return importe del IVA
     */
    public double getIva() { return iva; }

    /**
     * Establece el importe del IVA aplicado al pedido.
     *
     * @param iva importe del IVA
     */
    public void setIva(double iva) { this.iva = iva; }

    /**
     * Devuelve la lista de líneas de detalle del pedido.
     *
     * @return lista de líneas del pedido
     */
    public List<LineaPedido> getLineas() { return lineas; }

    /**
     * Establece la lista de líneas de detalle del pedido.
     *
     * @param lineas lista de líneas del pedido
     */
    public void setLineas(List<LineaPedido> lineas) { this.lineas = lineas; }

    /**
     * Devuelve una representación textual del objeto {@code Pedido}.
     *
     * @return cadena con el identificador, fecha, estado e importe del pedido
     */
    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", importe=" + importe +
                '}';
    }
}
