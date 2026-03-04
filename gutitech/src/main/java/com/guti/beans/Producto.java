package com.guti.beans;

/**
 * Bean que representa un producto disponible en la tienda.
 * <p>
 * Contiene toda la información comercial de un artículo: nombre, descripción,
 * precio, marca e imagen. Pertenece a una {@link Categoria} que lo agrupa
 * junto a productos similares.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see Categoria
 * @see com.guti.db.daos.ProductoDAO
 */
public class Producto {

    /** Identificador único del producto (clave primaria en BD). */
    private int idProducto;

    /** Categoría a la que pertenece el producto. */
    private Categoria categoria;

    /** Nombre comercial del producto. */
    private String nombre;

    /** Descripción técnica y comercial detallada del producto. */
    private String descripcion;

    /** Precio de venta al público sin IVA. */
    private double precio;

    /** Marca fabricante del producto. */
    private String marca;

    /**
     * Ruta relativa a la imagen del producto.
     * Formato: {@code imagenes/categoria/timestamp.jpg}
     */
    private String imagen;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Producto() {}

    /**
     * Constructor con todos los campos.
     *
     * @param idProducto  identificador único del producto
     * @param categoria   categoría a la que pertenece el producto
     * @param nombre      nombre comercial del producto
     * @param descripcion descripción técnica y comercial
     * @param precio      precio de venta sin IVA
     * @param marca       marca fabricante
     * @param imagen      ruta relativa a la imagen del producto
     */
    public Producto(int idProducto, Categoria categoria, String nombre,
                    String descripcion, double precio, String marca, String imagen) {
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.marca = marca;
        this.imagen = imagen;
    }

    /**
     * Devuelve el identificador único del producto.
     *
     * @return identificador del producto
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador único del producto.
     *
     * @param idProducto identificador del producto
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Devuelve la categoría a la que pertenece el producto.
     *
     * @return categoría del producto
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría a la que pertenece el producto.
     *
     * @param categoria categoría del producto
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Devuelve el nombre comercial del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre comercial del producto.
     *
     * @param nombre nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción técnica y comercial del producto.
     *
     * @return descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción técnica y comercial del producto.
     *
     * @param descripcion descripción del producto
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el precio de venta del producto sin IVA.
     *
     * @return precio sin IVA
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio de venta del producto sin IVA.
     *
     * @param precio precio sin IVA
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Devuelve la marca fabricante del producto.
     *
     * @return marca del producto
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca fabricante del producto.
     *
     * @param marca marca del producto
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Devuelve la ruta relativa a la imagen del producto.
     *
     * @return ruta de la imagen
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Establece la ruta relativa a la imagen del producto.
     *
     * @param imagen ruta de la imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * Devuelve una representación textual del objeto {@code Producto}.
     *
     * @return cadena con el identificador, nombre, precio y marca del producto
     */
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", marca='" + marca + '\'' +
                '}';
    }
}
