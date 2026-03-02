package com.guti.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int idPedido;
    private LocalDate fecha;
    private char estado;
    private int idUsuario;
    private double importe;
    private double iva;
    private List<LineaPedido> lineas;

    public Pedido() {
        this.lineas = new ArrayList<>();
    }

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

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public char getEstado() { return estado; }
    public void setEstado(char estado) { this.estado = estado; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public List<LineaPedido> getLineas() { return lineas; }
    public void setLineas(List<LineaPedido> lineas) { this.lineas = lineas; }

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
