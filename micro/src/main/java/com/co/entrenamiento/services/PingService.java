package com.co.entrenamiento.services;

import com.co.entrenamiento.core.DBPool;
import java.sql.SQLException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Logica de negocio de la prueba de vida. Aqui vive el SQL; el endpoint solo delega.
 */
public class PingService {

    /**
     * Respuesta estandar del proyecto: {"RESPUESTA": "...", "ESTADO": n}, con ESTADO > 0 = exito.
     */
    public JsonObject estado() {
        try {
            final JsonArray filas = DBPool.consultar("SELECT H2VERSION() AS VERSION");
            return Json.createObjectBuilder()
                    .add("RESPUESTA", "pong")
                    .add("ESTADO", 1)
                    .add("BASEDEDATOS", "H2 " + filas.getJsonObject(0).getString("VERSION"))
                    .build();
        } catch (final SQLException ex) {
            // Mensaje generico: no se expone el detalle interno al cliente.
            return Json.createObjectBuilder()
                    .add("RESPUESTA", "No se pudo consultar la base de datos")
                    .add("ESTADO", 0)
                    .build();
        }
    }
}
