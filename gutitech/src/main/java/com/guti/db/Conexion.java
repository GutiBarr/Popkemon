package com.guti.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Conexion {
    private static DataSource dataSource;

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