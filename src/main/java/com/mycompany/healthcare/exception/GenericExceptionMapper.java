/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.exception;

/**
 * Exception mapper for handling various exceptions and providing appropriate responses.
 * This class maps exceptions to corresponding HTTP status codes and error messages.
 *
 * @author Amandha
 */
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
        // Handling NotAllowedException
        if (exception instanceof NotAllowedException) {
            LOGGER.error("Method not allowed: " + exception.getMessage());
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .entity("Sorry, this action is not allowed. Error: " + exception.getMessage())
                    .build();
        } 
        // Handling NotFoundException
        else if (exception instanceof NotFoundException) {
            LOGGER.error("Resource not found: " + exception.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sorry, the requested resource was not found. Error: " + exception.getMessage())
                    .build();
        } 
        // Handling ForbiddenException
        else if (exception instanceof ForbiddenException) {
            LOGGER.error("Access forbidden: " + exception.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Sorry, you don't have permission to access this resource. Error: " + exception.getMessage())
                    .build();
        } 
        // Handling BadRequestException
        else if (exception instanceof BadRequestException) {
            LOGGER.error("Bad request: " + exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sorry, there was an error in your request. Error: " + exception.getMessage())
                    .build();
        } 
        // Handling NullPointerException
        else if (exception instanceof NullPointerException) {
            LOGGER.error("Null pointer exception: " + exception.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error when processing your request. The request body is empty or contains invalid data. Please provide valid data and try again.")
                    .build();
        }
        
        // Handling other unexpected exceptions
        LOGGER.error("An unexpected error occurred: " + exception.getMessage()  + " Exception: " + exception.toString());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Sorry, an unexpected error occurred. Error: " + exception.getMessage())
                .build();

    }
}
