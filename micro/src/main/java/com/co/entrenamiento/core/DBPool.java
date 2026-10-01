package com.co.entrenamiento.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * Unico punto de acceso a la base de datos (mismo papel que DBPool en el micro real).
 * Version minima: sin pool, sin transacciones. Se ira ampliando en las semanas siguientes.
 */
public final class DBPool {

    private static final String URL = System.getProperty("DB_URL", "jdbc:h2:mem:entrenamiento;DB_CLOSE_DELAY=-1");
    private static final String USER = System.getProperty("DB_USER", "sa");
    private static final String PASSWORD = System.getProperty("DB_PASSWORD", "");

    private DBPool() {
    }

    private static Connection abrir() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (final ClassNotFoundException ex) {
            throw new SQLException("Driver H2 no encontrado", ex);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Ejecuta un SELECT con parametros y devuelve las filas como JsonArray
     * (una JsonObject por fila, con los nombres de columna en mayusculas).
     */
    public static JsonArray consultar(final String sql, final Object... parametros) throws SQLException {
        try (Connection conn = abrir(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                final ResultSetMetaData meta = rs.getMetaData();
                final JsonArrayBuilder filas = Json.createArrayBuilder();
                while (rs.next()) {
                    final JsonObjectBuilder fila = Json.createObjectBuilder();
                    for (int c = 1; c <= meta.getColumnCount(); c++) {
                        final Object valor = rs.getObject(c);
                        final String nombre = meta.getColumnLabel(c);
                        if (valor == null) {
                            fila.addNull(nombre);
                        } else {
                            fila.add(nombre, valor.toString());
                        }
                    }
                    filas.add(fila);
                }
                return filas.build();
            }
        }
    }
}
