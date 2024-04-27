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
 *
 * @author Amandha
 */
@Provider
public class ModelIdMismatchExceptionMapper implements ExceptionMapper<ModelIdMismatchException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelIdMismatchExceptionMapper.class);
    
    @Override
    public Response toResponse(ModelIdMismatchException exception) {
        LOGGER.error("ModelIdMismatchException caught: {}",exception.getMessage(), exception);
        
        return Response.status(Response.Status.CONFLICT)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}