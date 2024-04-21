/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.MedicalRecordDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.dao.PersonDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import java.util.List;
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
import com.mycompany.healthcare.model.MedicalRecord;
import com.mycompany.healthcare.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
@Path("patients")
public class PatientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResource.class);

    private final PatientDAO patientDAO = new PatientDAO();
    private final PersonDAO personDAO = new PersonDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Patient> getAllPatients() {
        if (patientDAO.getAllPatients() != null) {
            return patientDAO.getAllPatients();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

    @GET
    @Path("/{patientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Patient getPatientById(@PathParam("patientId") int patientId) {
        LOGGER.info("Getting the patient by ID: " + patientId);
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient != null) {
            return patient;
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatient(Patient patient) {
        LOGGER.info("Adding a new patient");

        // Validate the appointment object
        String validationError = ValidationHelper.validate(patient);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPatientId = patientDAO.addPatient(patient); // add the new patient to the patients list
        personDAO.addPerson(patient); //add the new patient to the people list
        
        return Response.status(Response.Status.CREATED).entity("New patient with ID: " + patient + " was added successfully").build();
    }

    @PUT
    @Path("/{patientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatient(@PathParam("patientId") int patientId, Patient updatePatient) {
        Patient existingPatient = patientDAO.getPatientById(patientId);

        if (existingPatient != null) {
            updatePatient.setPatientId(patientId);
            patientDAO.updatePatient(updatePatient); // update the patient record

            Patient newUpdatedPatient = patientDAO.getPatientById(patientId);
            personDAO.updatePerson(newUpdatedPatient); //update the same patient record in person

            String message = "Patient with ID " + patientId + " was successfully updated";
            return Response.ok().entity(message).build(); // return a message indicating success
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    @DELETE
    @Path("/{patientId}")
    public Response deleteAppointment(@PathParam("patientId") int patientId) {
        boolean removed = patientDAO.deletePatient(patientId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Patient with ID " + patientId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    @GET
    @Path("/{patientId}/medicalRecord")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPatientMedicalRecord(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for medical record of patient with ID: " + patientId);
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordByPatientId(patientId); // check if patient already has a medical record

        if (existingMedicalRecord != null) {
            return Response.ok().entity(existingMedicalRecord).build();
        } else {
            throw new ResourceNotFoundException("Medical record of Patient with ID " + patientId + " was not found");
        }
    }

    @POST
    @Path("/{patientId}/medicalRecord")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatientMedicalRecord(@PathParam("patientId") int patientId, MedicalRecord medicalRecord) {

        LOGGER.info("Searching for patient with ID: " + patientId);
        Patient existingPatient = patientDAO.getPatientById(patientId); // check if the patient exists
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordByPatientId(patientId); // check if patient already has a medical record

        if (existingPatient != null && existingMedicalRecord == null) {
            LOGGER.info("Adding a new medical record");
            medicalRecord.setPatient(existingPatient);

            // Validate the medical record object
            String validationError = ValidationHelper.validate(medicalRecord);
            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
            }

            int newMedicalRecordId = medicalRecordDAO.addMedicalRecord(medicalRecord);
            return Response.status(Response.Status.CREATED).entity("New medical record with ID: " + newMedicalRecordId + " was added successfully").build();
        } else if (existingPatient == null) {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        } else {
            String message = "Patient with ID " + patientId + " already has a medical record";
            return Response.ok().entity(message).build();
        }

    }

    @PUT
    @Path("/{patientId}/medicalRecord")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatientMedicalRecord(@PathParam("patientId") int patientId, MedicalRecord updatedMedicalRecord) {

        Patient existingPatient = patientDAO.getPatientById(patientId); // check if the patient exists
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordByPatientId(patientId); // check if patient already has a medical record

        if (existingPatient != null && existingMedicalRecord != null) {
            if (updatedMedicalRecord.getMedicalRecordId() != existingMedicalRecord.getMedicalRecordId()) {
                throw new ModelIdMismatchException("Error Updating! Medical Record ID cannot be changed.");
            }
            updatedMedicalRecord.setPatient(existingPatient); //set the patient details in the medical record

            // Validate the medical record object
            String validationError = ValidationHelper.validate(updatedMedicalRecord);
            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
            }

            medicalRecordDAO.updateMedicalRecord(updatedMedicalRecord); //update the patients medical record

            String message = "Medical record of the  " + patientId + " was successfully updated";
            return Response.ok().entity(message).build(); // return a message indicating success
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

}
