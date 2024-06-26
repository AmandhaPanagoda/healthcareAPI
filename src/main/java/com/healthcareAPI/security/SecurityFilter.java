/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.security;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * This class implements a JAX-RS ContainerRequestFilter to perform basic
 * authentication for certain endpoints related to medical records, bills, and
 * prescriptions. It checks the Authorization header for a Basic authentication
 * token, decodes it, and validates the username and password. If the
 * credentials are valid, the request is allowed to proceed; otherwise, an
 * unauthorized response is returned.
 *
 * @author Amandha
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        boolean medicalRecords = requestContext.getUriInfo().getPath().contains("medical-records");
        boolean bills = requestContext.getUriInfo().getPath().contains("bills");
        boolean prescriptions = requestContext.getUriInfo().getPath().contains("prescriptions");

        if (medicalRecords || bills || prescriptions) { // authentication will apply for these endpoints
            List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
            if (authHeader != null && !authHeader.isEmpty()) {
                String authToken = authHeader.get(0);
                authToken = authToken.replace(AUTHORIZATION_HEADER_PREFIX, "");
                byte[] decodedBytes = Base64.getDecoder().decode(authToken);
                String decodedString = new String(decodedBytes);
                StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();

                if ("admin".equals(username) && "csarulz".equals(password)) {
                    return;
                }
            }
            Response unauthorizedStatus = Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("User is not allowed to access this resource.").build();

            requestContext.abortWith(unauthorizedStatus);
        }
    }
}
