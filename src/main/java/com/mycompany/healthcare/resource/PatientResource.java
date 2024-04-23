/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.AppointmentDAO;
import com.mycompany.healthcare.dao.MedicalRecordDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Appointment;
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
import javax.ws.rs.BadRequestException;
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
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Patient> getAllPatients() {
        LOGGER.info("Fetching all patient records");
        if (patientDAO.getAllPatients() != null) {
            return patientDAO.getAllPatients();
        } else {
            throw new ResourceNotFoundException("No patient records were found");
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
        if (patient == null) {
            throw new BadRequestException("Patients record cannot be null");
        }
        LOGGER.info("Adding a new patient");

        // Validate the appointment object
        String validationError = ValidationHelper.validate(patient);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPatientId = patientDAO.addPatient(patient); // add the new patient to the patients list

        return Response.status(Response.Status.CREATED).entity("New patient with ID: " + newPatientId + " was added successfully").build();
    }

    @PUT
    @Path("/{patientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatient(@PathParam("patientId") int patientId, Patient updatedPatient) {
        if (updatedPatient == null) {
            throw new BadRequestException("Patients record cannot be null");
        }

        // Validate the patient object
        String validationError = ValidationHelper.validate(updatedPatient);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        if (patientId != updatedPatient.getPatientId()) { // IDs are immutable when updating
            LOGGER.info("URL parameter patient ID and the passed patient ID do not match");
            return Response.status(Response.Status.CONFLICT).entity("IDs are immutable. The passed patient IDs do not match").build();
        }

        Patient existingPatient = patientDAO.getPatientById(patientId);

        if (existingPatient != null) {
            patientDAO.updatePatient(updatedPatient); // update the patient record
            String message = "Patient with ID " + patientId + " was successfully updated";
            return Response.ok().entity(message).build(); // return a message indicating success
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    @DELETE
    @Path("/{patientId}")
    public Response deletePatient(@PathParam("patientId") int patientId) {
        LOGGER.info("Deleting patient record ...");
        boolean removed = patientDAO.deletePatient(patientId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Patient with ID " + patientId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    /**
     * Retrieves the medical record of a patient with the specified ID.
     *
     * @param patientId The ID of the patient whose medical record is being
     * retrieved.
     * @return Response containing the medical record of the patient.
     * @throws ResourceNotFoundException if the medical record of the patient is
     * not found.
     */
    @GET
    @Path("/{patientId}/medical-records")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientMedicalRecord(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for medical record of patient with ID: " + patientId);
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordByPatientId(patientId); // check if patient already has a medical record

        if (existingMedicalRecord != null) {
            return Response.ok().entity(existingMedicalRecord).build();
        } else {
            throw new ResourceNotFoundException("Medical record of Patient with ID " + patientId + " was not found");
        }
    }

    /**
     * Adds a medical record for a patient with the specified ID.
     *
     * @param patientId The ID of the patient for whom the medical record is
     * being added.
     * @param medicalRecord The medical record to be added.
     * @return Response indicating the status of the operation.
     * @throws ResourceNotFoundException if the patient is not found.
     */
    @POST
    @Path("/{patientId}/medical-records")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatientMedicalRecord(@PathParam("patientId") int patientId, MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new BadRequestException("Medical record cannot be null");
        }

        LOGGER.info("Searching for patient with ID: " + patientId);
        Patient existingPatient = patientDAO.getPatientById(patientId); // check if the patient exists

        LOGGER.info("Searching for medical record of patient with ID: " + patientId);
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

    /**
     * Updates the medical record of a patient with the specified ID.
     *
     * @param patientId The ID of the patient whose medical record is being
     * updated.
     * @param updatedMedicalRecord The updated medical record.
     * @return Response indicating the status of the operation.
     * @throws ResourceNotFoundException if the patient or the medical record is
     * not found.
     */
    @PUT
    @Path("/{patientId}/medical-records")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatientMedicalRecord(@PathParam("patientId") int patientId, MedicalRecord updatedMedicalRecord) {

        Patient existingPatient = patientDAO.getPatientById(patientId); // check if the patient exists
        MedicalRecord existingMedicalRecord = medicalRecordDAO.getMedicalRecordByPatientId(patientId); // check if patient already has a medical record

        if (existingPatient != null && existingMedicalRecord != null) {
            if (updatedMedicalRecord.getMedicalRecordId() != existingMedicalRecord.getMedicalRecordId()) {
                LOGGER.info("URL parameter medical record ID and the passed medical record ID do not match");
                return Response.status(Response.Status.CONFLICT).entity("IDs are immutable. The passed medical record IDs do not match").build();
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

    @GET
    @Path("/{patientId}/appointments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientAppointments(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for appointments of patient with ID: " + patientId);
        List<Appointment> existingAppointments = appointmentDAO.getAppointmentbyPatientId(patientId); // get paatients appointments

        if (existingAppointments != null) {
            return Response.ok().entity(existingAppointments).build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " does not have any appointments");
        }
    }
}
