/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.MedicalRecordDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
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
import com.mycompany.healthcare.model.Patient;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTful resource class for managing medical records Provides endpoints for
 * CRUD operations and searching medical records based on various criteria.
 *
 * @author Amandha
 */
@Path("/medical-records")
public class MedicalRecordResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordResource.class);
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    /**
     * Retrieves all medical records.
     *
     * @return List of all medical records
     * @throws ResourceNotFoundException if no records are found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<MedicalRecord> getAllMedicalRecords() {
        LOGGER.info("Fetching all medical records");

        if (!medicalRecordDAO.getAllMedicalRecords().isEmpty()) {
            return medicalRecordDAO.getAllMedicalRecords().values(); //return all the medical records 
        } else {
            LOGGER.info("No medical records were found");
            throw new ResourceNotFoundException("No records were found");
        }
    }

    /**
     * Retrieves a specific medical record by ID.
     *
     * @param medicalRecordId The ID of the medical record to retrieve
     * @return The medical record with the specified ID
     * @throws ResourceNotFoundException if the record is not found
     */
    @GET
    @Path("/{medicalRecordId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MedicalRecord getMedicalRecordById(@PathParam("medicalRecordId") int medicalRecordId) {
        LOGGER.info("Getting the medical record by ID: " + medicalRecordId);

        MedicalRecord medicalRecord = medicalRecordDAO.getMedicalRecordById(medicalRecordId);
        if (medicalRecord != null) {
            return medicalRecord;
        } else {
            LOGGER.info("Medical Record with ID " + medicalRecordId + " was not found");
            throw new ResourceNotFoundException("Medical Record with ID " + medicalRecordId + " was not found");
        }
    }

    /**
     * Searches for medical records based on the provided criteria. If no
     * criteria are provided, returns all medical records.
     *
     * @param patientFirstName The first name of the patient to search for.
     * @param patientLastName The last name of the patient to search for.
     * @param bloodGroup The blood group of the patient to search for.
     * @return A response containing the matching medical records or a message
     * indicating no records were found.
     */
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
            return Response.ok(getAllMedicalRecords()).build(); //return all the medical records if filter criteria is not provided
        }

        try {
            List<MedicalRecord> matchingMedicalRecords = medicalRecordDAO.searchMedicalRecords(
                    patientFirstName, patientLastName, bloodGroup);

            if (!matchingMedicalRecords.isEmpty()) {
                return Response.ok(matchingMedicalRecords).build();
            } else {
                LOGGER.info("No medical records found with the given search criteria");
                throw new ResourceNotFoundException("No medical records found with the given search criteria");
            }
        } catch (BadRequestException e) {
            LOGGER.info("An error occured when retrieving medical records: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Adds a new medical record.
     *
     * @param medicalRecord The medical record object to add.
     * @return A response indicating the success or failure of the add
     * operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new BadRequestException("Medical Record cannot be null");
        }
        LOGGER.info("Adding a new medical record");

        // Validate the medical record object
        String validationError = ValidationHelper.validate(medicalRecord);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int patientId = medicalRecord.getPatient().getPersonId();
        Patient patient = patientDAO.getPatientById(patientId);

        if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }
        medicalRecord.setPatient(patient);

        int newMedicalRecordId = medicalRecordDAO.addMedicalRecord(medicalRecord);

        return Response.status(Response.Status.CREATED).entity("New medical record with ID: " + newMedicalRecordId + " was added successfully").build();
    }

    /**
     * Updates an existing medical record with the provided ID.
     *
     * @param medicalRecordId The ID of the medical record to update.
     * @param updatedMedicalRecord The updated medical record object.
     * @return A response indicating the success or failure of the update
     * operation.
     */
    @PUT
    @Path("/{medicalRecordId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMedicalRecord(@PathParam("medicalRecordId") int medicalRecordId, MedicalRecord updatedMedicalRecord) {
        if (updatedMedicalRecord == null) {
            throw new BadRequestException("Medical Record cannot be null");
        }

        if (medicalRecordId != updatedMedicalRecord.getMedicalRecordId()) { // IDs are immutable when updating
            LOGGER.info("URL parameter medical record ID and the passed medical record ID do not match");
            throw new ModelIdMismatchException("IDs are immutable. The passed medical record IDs do not match");
        }

        // Validate the medical record object
        String validationError = ValidationHelper.validate(updatedMedicalRecord);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        LOGGER.info("Updating medical record with ID: " + medicalRecordId);
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordById(medicalRecordId);

        if (existingMedicalRecord != null) { //check if the medical record exists
            int patientId = updatedMedicalRecord.getPatient().getPersonId();
            Patient patient = patientDAO.getPatientById(patientId);

            if (patient == null) {
                throw new ResourceNotFoundException("Patient does not exist");
            }
            updatedMedicalRecord.setPatient(patient);
            medicalRecordDAO.updateMedicalRecord(updatedMedicalRecord); // update the existing medical record

            LOGGER.info("Medical record was updated. Updated Medical Record ID: " + medicalRecordId);
            return Response.status(Response.Status.OK).entity("Medical Record with ID " + medicalRecordId + " was updated successfully").build();
        } else {
            LOGGER.error("Medical Record ID: " + medicalRecordId + " was not found");
            throw new ResourceNotFoundException("Medical Record with ID " + medicalRecordId + " was not found");
        }
    }

    /**
     * Deletes a medical record with the specified ID.
     *
     * @param medicalRecordId The ID of the medical record to delete
     * @return Response indicating the success or failure of the operation
     * @throws ResourceNotFoundException if the record is not found
     */
    @DELETE
    @Path("/{medicalRecordId}")
    public Response deleteMedicalRecord(@PathParam("medicalRecordId") int medicalRecordId) {
        LOGGER.info("Deleting medical record ...");
        boolean removed = medicalRecordDAO.deleteMedicalRecord(medicalRecordId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Medical Record with ID " + medicalRecordId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Medical Record with ID " + medicalRecordId + " was not found");
        }
    }
}
