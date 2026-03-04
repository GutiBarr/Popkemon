package com.guti.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Utilidad para gestionar el carrito de compra de usuarios no autenticados.
 * <p>
 * El carrito anónimo se almacena en una cookie del navegador del cliente
 * llamada {@value #NOMBRE_COOKIE}. El contenido se serializa a JSON con Gson
 * y se codifica con URL encoding para evitar caracteres inválidos en la cookie.
 * </p>
 * <p>
 * Formato del contenido de la cookie (antes de codificar):
 * {@code [{"idProducto":1,"cantidad":2},{"idProducto":5,"cantidad":1}]}
 * </p>
 * <p>
 * La cookie tiene una duración de 7 días ({@value #MAX_EDAD} segundos).
 * Al hacer login, el contenido se fusiona con el carrito del usuario
 * en la base de datos y la cookie se elimina.
 * </p>
 * <p>
 * Todos los métodos son estáticos; no es necesario instanciar la clase.
 * </p>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 * @see com.guti.servlets.CarritoServlet
 */
public class CarritoAnonimo {

    /** Nombre de la cookie que almacena el carrito anónimo. */
    private static final String NOMBRE_COOKIE = "carrito";

    /** Tiempo de vida de la cookie en segundos (2 días). */
    private static final int MAX_EDAD = 60 * 60 * 24 * 2;

    /** Instancia de Gson reutilizada para serializar y deserializar el carrito. */
    private static final Gson gson = new Gson();

    // =====================================================================
    // CLASE INTERNA
    // =====================================================================

    /**
     * Representa una línea del carrito anónimo almacenada en la cookie.
     * <p>
     * Contiene únicamente el identificador del producto y la cantidad,
     * ya que el carrito anónimo no almacena precios (se calculan al
     * renderizar la vista cargando el producto desde la BD).
     * Los campos son públicos para que Gson pueda serializarlos y
     * deserializarlos directamente. Los getters son necesarios para
     * que JSTL pueda acceder a los valores con {@code ${linea.idProducto}}.
     * </p>
     */
    public static class LineaAnonima {

        /** Identificador del producto. */
        public int idProducto;

        /** Cantidad de unidades del producto. */
        public int cantidad;

        /**
         * Constructor con todos los campos.
         *
         * @param idProducto identificador del producto
         * @param cantidad   cantidad de unidades
         */
        public LineaAnonima(int idProducto, int cantidad) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }

        /**
         * Devuelve el identificador del producto.
         * Necesario para acceso desde JSTL con {@code ${linea.idProducto}}.
         *
         * @return identificador del producto
         */
        public int getIdProducto() { return idProducto; }

        /**
         * Devuelve la cantidad de unidades del producto.
         * Necesario para acceso desde JSTL con {@code ${linea.cantidad}}.
         *
         * @return cantidad de unidades
         */
        public int getCantidad() { return cantidad; }
    }

    // =====================================================================
    // MÉTODOS PÚBLICOS
    // =====================================================================

    /**
     * Lee el contenido del carrito anónimo desde la cookie del cliente.
     * <p>
     * Recorre las cookies de la petición buscando la cookie {@value #NOMBRE_COOKIE},
     * decodifica su valor con URL decoding y lo deserializa desde JSON
     * a una lista de {@link LineaAnonima} con Gson.
     * </p>
     *
     * @param req petición HTTP del cliente
     * @return lista de líneas del carrito anónimo,
     *         o lista vacía si la cookie no existe o no se puede leer
     */
    public static List<LineaAnonima> leer(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if (NOMBRE_COOKIE.equals(c.getName())) {
                    try {
                        String json = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        return gson.fromJson(json, new TypeToken<List<LineaAnonima>>(){}.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Serializa la lista de líneas a JSON y la guarda en la cookie del cliente.
     * <p>
     * El JSON se codifica con URL encoding antes de almacenarlo para evitar
     * caracteres especiales no válidos en el valor de una cookie HTTP.
     * La cookie se configura con duración {@value #MAX_EDAD} segundos (7 días)
     * y path {@code /} para que sea accesible desde toda la aplicación.
     * </p>
     *
     * @param resp  respuesta HTTP donde se añade la cookie actualizada
     * @param lineas lista de líneas a serializar y guardar
     */
    public static void guardar(HttpServletResponse resp, List<LineaAnonima> lineas) {
        String json = URLEncoder.encode(gson.toJson(lineas), StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(NOMBRE_COOKIE, json);
        cookie.setMaxAge(MAX_EDAD);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    /**
     * Añade un producto al carrito anónimo o incrementa su cantidad si ya existe.
     * <p>
     * Lee el carrito actual, busca si el producto ya tiene línea y suma
     * la cantidad indicada. Si no existe, crea una nueva {@link LineaAnonima}.
     * Finalmente guarda el carrito actualizado en la cookie.
     * </p>
     *
     * @param req        petición HTTP del cliente (para leer la cookie actual)
     * @param resp       respuesta HTTP (para escribir la cookie actualizada)
     * @param idProducto identificador del producto a añadir
     * @param cantidad   número de unidades a añadir
     */
    public static void anadir(HttpServletRequest req, HttpServletResponse resp,
                               int idProducto, int cantidad) {
        List<LineaAnonima> lineas = leer(req);
        boolean encontrado = false;
        for (LineaAnonima l : lineas) {
            if (l.idProducto == idProducto) {
                l.cantidad += cantidad;
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            lineas.add(new LineaAnonima(idProducto, cantidad));
        }
        guardar(resp, lineas);
    }

    /**
     * Elimina un producto del carrito anónimo.
     * <p>
     * Lee el carrito actual, elimina la línea cuyo {@code idProducto}
     * coincida con el indicado usando {@code removeIf} y guarda
     * el carrito actualizado en la cookie.
     * </p>
     *
     * @param req        petición HTTP del cliente
     * @param resp       respuesta HTTP
     * @param idProducto identificador del producto a eliminar
     */
    public static void eliminar(HttpServletRequest req, HttpServletResponse resp,
                                 int idProducto) {
        List<LineaAnonima> lineas = leer(req);
        lineas.removeIf(l -> l.idProducto == idProducto);
        guardar(resp, lineas);
    }

    /**
     * Vacía completamente el carrito anónimo eliminando la cookie del cliente.
     * <p>
     * Crea una nueva cookie con el mismo nombre y valor vacío, y le asigna
     * {@code MaxAge = 0} para que el navegador la elimine inmediatamente.
     * </p>
     *
     * @param resp respuesta HTTP donde se sobreescribe la cookie con MaxAge 0
     */
    public static void vaciar(HttpServletResponse resp) {
        Cookie cookie = new Cookie(NOMBRE_COOKIE, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }
}
