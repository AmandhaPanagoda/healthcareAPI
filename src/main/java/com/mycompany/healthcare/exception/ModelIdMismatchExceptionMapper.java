/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception mapper for handling ModelIdMismatchException.
 * This class maps the ModelIdMismatchException to a 409 (Conflict) HTTP status code and returns the exception message as the response entity.
 *
 * @author Amandha
 */
@Provider
public class ModelIdMismatchExceptionMapper implements ExceptionMapper<ModelIdMismatchException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelIdMismatchExceptionMapper.class);
    
    @Override
    public Response toResponse(ModelIdMismatchException exception) {
        // Log the exception
        LOGGER.error("ModelIdMismatchException caught: {}",exception.getMessage());
        
        // Build and return the response
        return Response.status(Response.Status.CONFLICT)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}