package com.co.entrenamiento.services;

import com.co.entrenamiento.core.Claves;
import com.co.entrenamiento.core.DBPool;
import java.sql.SQLException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Logica de negocio del login. Aqui vive el SQL; el endpoint solo delega.
 */
public class LoginService {

    /**
     * Respuesta estandar del proyecto: {"RESPUESTA": "...", "ESTADO": n}, con ESTADO > 0 = exito.
     * Nunca devuelve la clave, ni en exito ni en error.
     */
    public JsonObject login(final String usuario, final String clave) {
        if (esVacio(usuario) || esVacio(clave)) {
            return error("Debe ingresar usuario y clave");
        }
        try {
            final JsonArray filas = DBPool.consultar(
                    "SELECT ID, USUARIO, NOMBRE, ACTIVO FROM USUARIOS WHERE USUARIO = ? AND CLAVE = ?",
                    usuario.trim(), Claves.hash(clave));
            if (filas.isEmpty()) {
                // Mensaje generico a proposito: no revela si el usuario existe o si la clave es la que falla.
                return error("Usuario o clave incorrectos");
            }
            final JsonObject fila = filas.getJsonObject(0);
            if (!Boolean.parseBoolean(fila.getString("ACTIVO"))) {
                return error("El usuario esta inactivo");
            }
            return Json.createObjectBuilder()
                    .add("RESPUESTA", "Bienvenido, " + fila.getString("NOMBRE"))
                    .add("ESTADO", 1)
                    .add("USUARIO", Json.createObjectBuilder()
                            .add("ID", fila.getString("ID"))
                            .add("USUARIO", fila.getString("USUARIO"))
                            .add("NOMBRE", fila.getString("NOMBRE")))
                    .build();
        } catch (final SQLException ex) {
            // Mensaje generico: no se expone el detalle interno al cliente.
            return error("No se pudo validar el usuario");
        }
    }

    private boolean esVacio(final String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private JsonObject error(final String mensaje) {
        return Json.createObjectBuilder().add("RESPUESTA", mensaje).add("ESTADO", 0).build();
    }
}
