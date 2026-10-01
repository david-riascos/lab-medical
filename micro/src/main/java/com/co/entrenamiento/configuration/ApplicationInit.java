package com.co.entrenamiento.configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Activa JAX-RS. Todos los endpoints quedan bajo /api (igual que en el micro real).
 */
@ApplicationPath("api")
public class ApplicationInit extends Application {

}
