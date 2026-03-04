package com.guti.beans;

/**
 * Bean que representa una línea de detalle dentro de un pedido.
 * <p>
 * Cada línea asocia un {@link Producto} con una cantidad determinada
 * dentro de un {@link Pedido}. El subtotal se calcula multiplicando
 * el precio del producto por la cantidad solicitada.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see Pedido
 * @see Producto
 */
public class LineaPedido {

    /** Identificador único de la línea (clave primaria en BD). */
    private int idLinea;

    /** Identificador del pedido al que pertenece esta línea (clave foránea). */
    private int idPedido;

    /** Producto incluido en esta línea del pedido. */
    private Producto producto;

    /** Cantidad de unidades del producto en esta línea. */
    private int cantidad;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public LineaPedido() {}

    /**
     * Constructor con todos los campos.
     *
     * @param idLinea   identificador único de la línea
     * @param idPedido  identificador del pedido al que pertenece
     * @param producto  producto asociado a esta línea
     * @param cantidad  número de unidades del producto
     */
    public LineaPedido(int idLinea, int idPedido, Producto producto, int cantidad) {
        this.idLinea = idLinea;
        this.idPedido = idPedido;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el identificador único de la línea.
     *
     * @return identificador de la línea
     */
    public int getIdLinea() { return idLinea; }

    /**
     * Establece el identificador único de la línea.
     *
     * @param idLinea identificador de la línea
     */
    public void setIdLinea(int idLinea) { this.idLinea = idLinea; }

    /**
     * Devuelve el identificador del pedido al que pertenece esta línea.
     *
     * @return identificador del pedido
     */
    public int getIdPedido() { return idPedido; }

    /**
     * Establece el identificador del pedido al que pertenece esta línea.
     *
     * @param idPedido identificador del pedido
     */
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    /**
     * Devuelve el producto asociado a esta línea.
     *
     * @return producto de la línea
     */
    public Producto getProducto() { return producto; }

    /**
     * Establece el producto asociado a esta línea.
     *
     * @param producto producto de la línea
     */
    public void setProducto(Producto producto) { this.producto = producto; }

    /**
     * Devuelve la cantidad de unidades del producto en esta línea.
     *
     * @return cantidad de unidades
     */
    public int getCantidad() { return cantidad; }

    /**
     * Establece la cantidad de unidades del producto en esta línea.
     *
     * @param cantidad cantidad de unidades
     */
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /**
     * Calcula el subtotal de esta línea sin IVA.
     *
     * @return precio del producto multiplicado por la cantidad
     */
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    /**
     * Devuelve una representación textual del objeto {@code LineaPedido}.
     *
     * @return cadena con el identificador de línea, nombre del producto y cantidad
     */
    @Override
    public String toString() {
        return "LineaPedido{idLinea=" + idLinea + ", producto=" + producto.getNombre() + ", cantidad=" + cantidad + '}';
    }
}