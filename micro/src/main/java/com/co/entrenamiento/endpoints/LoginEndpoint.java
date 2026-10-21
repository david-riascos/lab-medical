package com.co.entrenamiento.endpoints;

import com.co.entrenamiento.services.LoginService;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * POST /api/login -> valida usuario y clave contra la tabla USUARIOS.
 * Capa de endpoints: solo traduce HTTP; la logica esta en LoginService.
 */
@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginEndpoint {

    private final LoginService loginService = new LoginService();

    @POST
    public Response login(final JsonObject cuerpo) {
        final String usuario = cuerpo != null ? cuerpo.getString("usuario", null) : null;
        final String clave = cuerpo != null ? cuerpo.getString("clave", null) : null;
        final JsonObject respuesta = loginService.login(usuario, clave);
        if (respuesta.getInt("ESTADO") > 0) {
            return Response.ok(respuesta).build();
        }
        // ESTADO 0 se traduce a HTTP 400, como en el micro real.
        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta).build();
    }
}
