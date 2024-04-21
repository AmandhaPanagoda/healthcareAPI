/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.MedicalRecordDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.healthcare.model.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
@Path("/medical-records")
public class MedicalRecordResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordResource.class);
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MedicalRecord> getAllMedicalRecords() {
        if (medicalRecordDAO.getAllMedicalRecords() != null) {
            return medicalRecordDAO.getAllMedicalRecords();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

    @GET
    @Path("/{medicalRecordId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MedicalRecord getMedicalRecordById(@PathParam("medicalRecordId") int medicalRecordId) {
        LOGGER.info("Getting the medical record by ID: " + medicalRecordId);
        MedicalRecord medicalRecord = medicalRecordDAO.getMedicalRecordById(medicalRecordId);
        if (medicalRecord != null) {
            return medicalRecord;
        } else {
            throw new ResourceNotFoundException("Medical Record with ID " + medicalRecordId + " was not found");
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMedicalRecord(
            @QueryParam("patientFirstName") String patientFirstName,
            @QueryParam("patientLastName") String patientLastName,
            @QueryParam("bloodGroup") String bloodGroup) {

        LOGGER.info("Searching for medical records in the given criteria: patientFirstName: " + patientFirstName + ", patientLastName: " + patientLastName + ", bloodGroup: " + bloodGroup);

        if ((patientFirstName == null || patientFirstName.isEmpty())
                && (patientLastName == null || patientLastName.isEmpty())
                && (bloodGroup == null || bloodGroup.isEmpty())) {
            return Response.ok(getAllMedicalRecords()).build();
        }

        try {
            List<MedicalRecord> matchingMedicalRecords = medicalRecordDAO.searchMedicalRecords(
                    patientFirstName, patientLastName, bloodGroup);
            if (!matchingMedicalRecords.isEmpty()) {
                return Response.ok(matchingMedicalRecords).build();
            } else {
                LOGGER.info("No medical records found with the given search criteria");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No medical records found with the given search criteria")
                        .build();
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{medicalRecordId}")
    public Response deleteMedicalRecord(@PathParam("medicalRecordId") int medicalRecordId) {
        boolean removed = medicalRecordDAO.deleteMedicalRecord(medicalRecordId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Medical Record with ID " + medicalRecordId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Medical Record with ID " + medicalRecordId + " was not found");
        }
    }

}
