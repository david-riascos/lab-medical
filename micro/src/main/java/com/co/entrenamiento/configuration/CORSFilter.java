package com.co.entrenamiento.configuration;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Permite que el frontend (Nuxt en desarrollo, puerto 3001) llame a este backend (puerto 8081).
 * A diferencia del micro real, aqui el origen permitido es uno solo y configurable
 * (-DORIGEN_PERMITIDO=...), no "*".
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

    private static final String ORIGEN_PERMITIDO = System.getProperty("ORIGEN_PERMITIDO", "http://localhost:3001");
    private static final String METODOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final String CABECERAS = "authorization,origin,accept,content-type";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", ORIGEN_PERMITIDO);
        headers.add("Access-Control-Allow-Headers", CABECERAS);
        headers.add("Access-Control-Allow-Methods", METODOS);
        headers.add("Access-Control-Max-Age", 42 * 60 * 60);
    }
}
