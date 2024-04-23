/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.PrescriptionDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.healthcare.model.Prescription;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
@Path("/prescriptions")
public class PrescriptionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionResource.class);
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Prescription> getAllPrescriptions() {
        if (prescriptionDAO.getAllPrescriptions() != null) {
            return prescriptionDAO.getAllPrescriptions().values();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

    @GET
    @Path("/{prescriptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Prescription getPrescriptionById(@PathParam("prescriptionId") int prescriptionId) {
        LOGGER.info("Getting the prescription by ID: " + prescriptionId);
        Prescription prescription = prescriptionDAO.getPrescriptionById(prescriptionId);
        if (prescription != null) {
            return prescription;
        } else {
            throw new ResourceNotFoundException("Prescription with ID " + prescriptionId + " was not found");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPrescription(Prescription prescription) {
        LOGGER.info("Adding a new prescription");

        // Validate the prescription object
        String validationError = ValidationHelper.validate(prescription);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPrescriptionId = prescriptionDAO.addPrescription(prescription);
        return Response.status(Response.Status.CREATED).entity("New prescription with ID: " + newPrescriptionId + " was added successfully").build();
    }

    @PUT
    @Path("/{prescriptionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePrescription(@PathParam("prescriptionId") int prescriptionId, Prescription updatedPrescription) {
        if (prescriptionId != updatedPrescription.getPrescriptionId()) {
            LOGGER.info("URL parameter prescription ID and the passed prescription ID do not match");
            return Response.status(Response.Status.OK).entity("The passed prescription IDs do not match").build();
        }
        LOGGER.info("Updating prescription with ID: " + prescriptionId);
        Prescription existingPrescription = prescriptionDAO.getPrescriptionById(prescriptionId);

        if (existingPrescription != null) {
            updatedPrescription.setPrescriptionId(prescriptionId);
            prescriptionDAO.updatePrescription(updatedPrescription);
            LOGGER.info("Prescription record was updated. Updated Prescription ID: " + prescriptionId);
            return Response.status(Response.Status.OK).entity("Prescription with ID " + prescriptionId + " was updated successfully").build();
        } else {
            LOGGER.error("Prescription ID" + prescriptionId + " was not found");
            throw new ResourceNotFoundException("Prescription with ID " + prescriptionId + " was not found");
        }
    }

    @DELETE
    @Path("/{prescriptionId}")
    public Response deletePrescription(@PathParam("prescriptionId") int prescriptionId) {
        boolean removed = prescriptionDAO.deletePrescription(prescriptionId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Prescription with ID " + prescriptionId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Prescription with ID " + prescriptionId + " was not found");
        }
    }
}
