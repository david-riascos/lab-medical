package com.co.entrenamiento.endpoints;

import com.co.entrenamiento.services.PingService;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * GET /api/ping -> prueba que el backend y la base de datos responden.
 * Capa de endpoints: solo traduce HTTP; la logica esta en PingService.
 */
@Path("ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingEndpoint {

    private final PingService pingService = new PingService();

    @GET
    public Response ping() {
        final JsonObject respuesta = pingService.estado();
        if (respuesta.getInt("ESTADO") > 0) {
            return Response.ok(respuesta).build();
        }
        // ESTADO 0 se traduce a HTTP 400, como en el micro real.
        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta).build();
    }
}
