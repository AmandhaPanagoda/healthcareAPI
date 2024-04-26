/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.exception;

/**
 *
 * @author Amandha
 */
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof NotAllowedException) {
            LOGGER.warn("Method not allowed: " + exception.getMessage());
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .entity("Sorry, this action is not allowed. Error: " + exception.getMessage())
                    .build();
        } else if (exception instanceof NotFoundException) {
            LOGGER.warn("Resource not found: " + exception.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sorry, the requested resource was not found. Error: " + exception.getMessage())
                    .build();
        } else if (exception instanceof ForbiddenException) {
            LOGGER.warn("Access forbidden: " + exception.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Sorry, you don't have permission to access this resource. Error: " + exception.getMessage())
                    .build();
        } else if (exception instanceof BadRequestException) {
            LOGGER.warn("Bad request: " + exception.getMessage());
            if (exception instanceof JsonMappingException) {
                JsonMappingException jsonException = (JsonMappingException) exception;
                if (jsonException instanceof UnrecognizedPropertyException) {
                    String fieldName = ((UnrecognizedPropertyException) jsonException).getPropertyName();
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Unrecognized field '" + fieldName + "' in the request. Error: " + exception.getMessage())
                            .build();
                }
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sorry, there was an error in your request. Error: " + exception.getMessage())
                    .build();
        }

        LOGGER.error("An unexpected error occurred: " + exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Sorry, an unexpected error occurred. Error: " + exception.getMessage())
                .build();

    }
}
