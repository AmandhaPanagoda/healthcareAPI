/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

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
import java.util.Collection;
import javax.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mycompany.healthcare.model.MedicalRecord;
import com.mycompany.healthcare.model.Patient;
import com.mycompany.healthcare.model.Prescription;
import com.mycompany.healthcare.dao.AppointmentDAO;
import com.mycompany.healthcare.dao.BillingDAO;
import com.mycompany.healthcare.dao.MedicalRecordDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.dao.PrescriptionDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Appointment;
import com.mycompany.healthcare.model.Billing;
import javax.ws.rs.QueryParam;

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
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final BillingDAO billingDAO = new BillingDAO();

    /**
     * Retrieves all patient records.
     *
     * @return A collection of all patients in JSON format.
     * @throws ResourceNotFoundException if no patient records were found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Patient> getAllPatients() {
        LOGGER.info("Fetching all patient records");
        if (patientDAO.getAllPatients() != null) {
            return patientDAO.getAllPatients().values();
        } else {
            throw new ResourceNotFoundException("No patient records were found");
        }
    }

    /**
     * Retrieves a patient by ID.
     *
     * @param patientId The ID of the patient to retrieve.
     * @return The patient with the specified ID.
     * @throws ResourceNotFoundException if the patient with the specified ID
     * was not found.
     */
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

    /**
     * Adds a new patient record.
     *
     * @param patient The patient to add.
     * @return A response indicating the success of the operation.
     * @throws BadRequestException if the patient record is null.
     */
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

    /**
     * Updates an existing patient record.
     *
     * @param patientId The ID of the patient to update.
     * @param updatedPatient The updated patient record.
     * @return A response indicating the success of the operation.
     * @throws BadRequestException if the updated patient record is null.
     * @throws ResourceNotFoundException if the patient with the specified ID
     * was not found.
     */
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

    /**
     * Deletes a patient record.
     *
     * @param patientId The ID of the patient to delete.
     * @return A response indicating the success of the operation.
     * @throws ResourceNotFoundException if the patient with the specified ID
     * was not found.
     */
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
     * Searches for patients based on the specified criteria.
     *
     * @param firstName The first name of the patient (optional).
     * @param lastName The last name of the patient (optional).
     * @param minAge The minimum age of the patient (inclusive, optional).
     * @param maxAge The maximum age of the patient (inclusive, optional).
     * @param gender The gender of the patient (optional).
     * @return A response containing the list of patients that match the specified criteria,
     * or an appropriate error response if the search fails.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPatients(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("minAge") Integer minAge,
            @QueryParam("maxAge") Integer maxAge,
            @QueryParam("gender") String gender) {

        LOGGER.info("Searching for patients with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + "and gender: " + gender);

        if ((firstName == null || firstName.isEmpty())
                && (lastName == null || lastName.isEmpty())
                && (minAge == null)
                && (maxAge == null)
                && (gender == null || gender.isEmpty())) {
            return Response.ok(getAllPatients()).build();
        }

        try {
            List<Patient> matchingPatients = patientDAO.searchPatients(firstName, lastName, minAge, maxAge, gender);
            if (!matchingPatients.isEmpty()) {
                return Response.ok(matchingPatients).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No patients found with the given search criteria")
                        .build();
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
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
     * Retrieves all appointments of a patient based on the patient's ID.
     *
     * @param patientId The ID of the patient.
     * @return A Response containing the list of appointments in JSON format.
     * @throws ResourceNotFoundException if the patient does not have any
     * appointments.
     */
    @GET
    @Path("/{patientId}/appointments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientAppointments(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for appointments of patient with ID: " + patientId);
        List<Appointment> existingAppointments = appointmentDAO.getAppointmentByPatientId(patientId); // get patients appointments

        if (existingAppointments != null) {
            return Response.ok().entity(existingAppointments).build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " does not have any appointments");
        }
    }

    /**
     * Retrieves all prescriptions of a patient based on the patient's ID.
     *
     * @param patientId The ID of the patient.
     * @return A Response containing the list of prescriptions in JSON format.
     * @throws ResourceNotFoundException if the patient does not have any
     * prescriptions.
     */
    @GET
    @Path("/{patientId}/prescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientPrescriptions(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for prescriptions of patient with ID: " + patientId);
        List<Prescription> existingPrescriptions = prescriptionDAO.getPrescriptionByPatientId(patientId); // get patients prescriptions

        if (existingPrescriptions != null) {
            return Response.ok().entity(existingPrescriptions).build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " does not have any prescriptions");
        }
    }

    /**
     * Retrieves all bills of a patient based on the patient's ID.
     *
     * @param patientId The ID of the patient.
     * @return A Response containing the list of bills in JSON format.
     * @throws ResourceNotFoundException if the patient does not have any bills.
     */
    @GET
    @Path("/{patientId}/bills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientBills(@PathParam("patientId") int patientId) {
        LOGGER.info("Searching for bills of patient with ID: " + patientId);
        List<Billing> existingBills = billingDAO.getBillByPatientId(patientId); // get patients bills

        if (existingBills != null) {
            return Response.ok().entity(existingBills).build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " does not have any bills");
        }
    }
}
