package com.co.entrenamiento.configuration;

import com.co.entrenamiento.core.Claves;
import com.co.entrenamiento.core.DBPool;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Crea la tabla USUARIOS y siembra un usuario ficticio al arrancar el war.
 * Necesario porque la BD es H2 en memoria: se pierde cada vez que se apaga el servidor.
 */
@WebListener
public class EsquemaInicial implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        try {
            DBPool.ejecutar(
                    "CREATE TABLE IF NOT EXISTS USUARIOS ("
                            + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                            + "USUARIO VARCHAR(50) NOT NULL UNIQUE, "
                            + "CLAVE VARCHAR(64) NOT NULL, "
                            + "NOMBRE VARCHAR(100) NOT NULL, "
                            + "ACTIVO BOOLEAN NOT NULL DEFAULT TRUE)");

            // Usuario ficticio de prueba. MERGE ... KEY(USUARIO) hace que sea idempotente:
            // no duplica la fila si el listener corre mas de una vez.
            DBPool.ejecutar(
                    "MERGE INTO USUARIOS (USUARIO, CLAVE, NOMBRE, ACTIVO) KEY(USUARIO) VALUES (?, ?, ?, ?)",
                    "admin", Claves.hash("admin123"), "Administrador (ficticio)", true);
        } catch (final SQLException ex) {
            throw new IllegalStateException("No se pudo inicializar la tabla USUARIOS", ex);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        // Nada que liberar: DBPool abre y cierra su propia conexion en cada llamada.
    }
}
