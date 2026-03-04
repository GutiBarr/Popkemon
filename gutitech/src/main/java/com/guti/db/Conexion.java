package com.guti.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Clase de acceso a la base de datos mediante un pool de conexiones JNDI.
 * <p>
 * Obtiene el {@link DataSource} configurado en Tomcat a través de JNDI
 * con el nombre {@code java:/comp/env/jdbc/MySQLDS}. El pool de conexiones
 * es gestionado por el propio servidor de aplicaciones, lo que evita
 * crear y destruir conexiones en cada petición.
 * </p>
 * <p>
 * El {@code DataSource} se inicializa una única vez en el bloque estático
 * al cargar la clase. Si la búsqueda JNDI falla, se lanza una
 * {@link RuntimeException} que impide el arranque de la aplicación.
 * </p>
 * <p>
 * Configuración necesaria en {@code context.xml} de Tomcat:
 * </p>
 * <pre>
 * &lt;Resource name="jdbc/MySQLDS"
 *           auth="Container"
 *           type="javax.sql.DataSource"
 *           driverClassName="com.mysql.cj.jdbc.Driver"
 *           url="jdbc:mysql://localhost:3306/tienda_on_line"
 *           username="root" password="..." /&gt;
 * </pre>
 *
 * @author José María Gutiérrez Barrena
 * @version 1.0
 */
public class Conexion {

    /** DataSource obtenido del servidor de aplicaciones mediante JNDI. */
    private static DataSource dataSource;

    /*
     * Bloque de inicialización estático.
     * Se ejecuta una única vez al cargar la clase en memoria.
     * Busca el DataSource en el contexto JNDI de Tomcat.
     */
    static {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(
                "java:/comp/env/jdbc/MySQLDS"
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener DataSource", e);
        }
    }

    /**
     * Obtiene una conexión disponible del pool de conexiones.
     * <p>
     * La conexión debe cerrarse siempre al terminar de usarla,
     * preferiblemente mediante un bloque {@code try-with-resources},
     * para devolverla al pool y no agotarlo.
     * </p>
     * <p>Ejemplo de uso correcto:</p>
     * <pre>
     * try (Connection con = Conexion.getConexion()) {
     *     // usar la conexión
     * }
     * </pre>
     *
     * @return una {@link Connection} activa del pool,
     *         o {@code null} si no se puede obtener ninguna
     */
    public static Connection getConexion() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}