package com.guti.beans;

public class LineaPedido {

    private int idLinea;
    private int idPedido;
    private Producto producto;
    private int cantidad;

    public LineaPedido() {}

    public LineaPedido(int idLinea, int idPedido, Producto producto, int cantidad) {
        this.idLinea = idLinea;
        this.idPedido = idPedido;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public int getIdLinea() { return idLinea; }
    public void setIdLinea(int idLinea) { this.idLinea = idLinea; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return "LineaPedido{idLinea=" + idLinea + ", producto=" + producto.getNombre() + ", cantidad=" + cantidad + '}';
    }
}