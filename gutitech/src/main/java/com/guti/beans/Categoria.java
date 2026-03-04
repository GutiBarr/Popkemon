package com.guti.beans;

/**
 * Bean que representa una categoría de productos de la tienda.
 * <p>
 * Cada categoría agrupa un conjunto de productos relacionados
 * (por ejemplo, Procesadores, Tarjetas Gráficas, Monitores, etc.).
 * Las categorías se cargan al iniciar la aplicación mediante
 * {@code StartListener} y se almacenan en el contexto para
 * estar disponibles en todos los JSP.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
public class Categoria {

    /** Identificador único de la categoría (clave primaria en BD). */
    private int idCategoria;

    /** Nombre descriptivo de la categoría. */
    private String nombre;

    /** Ruta relativa a la imagen representativa de la categoría. */
    private String imagen;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Categoria() {}

    /**
     * Constructor con todos los campos.
     *
     * @param idCategoria identificador único de la categoría
     * @param nombre      nombre descriptivo de la categoría
     * @param imagen      ruta relativa a la imagen de la categoría
     */
    public Categoria(int idCategoria, String nombre, String imagen) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    /**
     * Devuelve el identificador único de la categoría.
     *
     * @return identificador de la categoría
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador único de la categoría.
     *
     * @param idCategoria identificador de la categoría
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Devuelve el nombre de la categoría.
     *
     * @return nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre nombre de la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la ruta relativa a la imagen de la categoría.
     *
     * @return ruta de la imagen
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Establece la ruta relativa a la imagen de la categoría.
     *
     * @param imagen ruta de la imagen
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * Devuelve una representación textual del objeto {@code Categoria}.
     *
     * @return cadena con los valores de todos los campos
     */
    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}