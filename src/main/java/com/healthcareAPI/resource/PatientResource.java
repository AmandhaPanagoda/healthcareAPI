/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.resource;

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
import com.healthcareAPI.model.MedicalRecord;
import com.healthcareAPI.model.Patient;
import com.healthcareAPI.model.Prescription;
import com.healthcareAPI.dao.AppointmentDAO;
import com.healthcareAPI.dao.BillingDAO;
import com.healthcareAPI.dao.MedicalRecordDAO;
import com.healthcareAPI.dao.PatientDAO;
import com.healthcareAPI.dao.PersonDAO;
import com.healthcareAPI.dao.PrescriptionDAO;
import com.healthcareAPI.exception.ModelIdMismatchException;
import com.healthcareAPI.exception.ResourceNotFoundException;
import com.healthcareAPI.helper.ValidationHelper;
import com.healthcareAPI.model.Appointment;
import com.healthcareAPI.model.Billing;
import com.healthcareAPI.model.Person;
import javax.ws.rs.PATCH;
import javax.ws.rs.QueryParam;

/**
 * Resource class for managing patient records. Provides endpoints for
 * retrieving, adding, updating, and deleting patient records.
 *
 * @author Amandha
 */
@Path("patients")
public class PatientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResource.class);

    private final PatientDAO patientDAO = new PatientDAO();
    private final PersonDAO personDAO = new PersonDAO();
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
        if (!patientDAO.getAllPatients().isEmpty()) {
            LOGGER.info("Fetching all patient records");
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
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient != null) {
            LOGGER.info("Getting the patient by ID: " + patientId);
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
            LOGGER.error("Patient object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Patient cannot be null").build();
        }

        // Validate the patient object
        String validationError = ValidationHelper.validate(patient);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        Person person = createPerson(patient); // create a person object 
        int newPatientId = personDAO.addPerson(person); // add the new person to person record

        if (newPatientId != -1) {
            patient.setPersonId(newPatientId); // set the new person ID of the patient
            patientDAO.addPatient(patient); // add the new patient to the patients list 

            return Response.status(Response.Status.CREATED).entity("New patient with ID: " + newPatientId + " was added successfully").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occured when adding the patient").build();
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
            LOGGER.error("Patient object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Patient cannot be null").build();
        }

        if (patientId != updatedPatient.getPersonId()) { // IDs are immutable when updating
            throw new ModelIdMismatchException("The passed patient IDs do not match");
        }

        // Validate the patient object
        String validationError = ValidationHelper.validate(updatedPatient);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        // get the existing patient record
        Patient existingPatient = patientDAO.getPatientById(patientId);

        if (existingPatient != null) {
            Person updatedPerson = createPerson(updatedPatient); // create a person object
            updatedPerson.setPersonId(patientId); // set the person id of the person object
            patientDAO.updatePatient(updatedPatient); // update the patient record
            personDAO.updatePerson(updatedPerson); // update the person record

            LOGGER.info("Patient record was updated. Updated Patient ID: " + patientId);
            return Response.status(Response.Status.OK).entity("Patient with ID " + patientId + " was updated successfully").build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " was not found");
        }
    }

    /**
     * Partially updates a patient record.
     *
     * @param patientId The ID of the patient to update.
     * @param partialUpdatedPatient The partially updated Patient object
     * containing the new values.
     * @return A Response indicating the outcome of the update operation.
     */
    @PATCH
    @Path("/{patientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response partialUpdatePatient(@PathParam("patientId") int patientId, Patient partialUpdatedPatient) {
        if (partialUpdatedPatient == null) {
            LOGGER.error("Patient object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Patient cannot be null").build();
        }

        if (patientId != partialUpdatedPatient.getPersonId()) {
            throw new ModelIdMismatchException("The passed patient IDs do not match");
        }

        // Get the existing patient record
        Patient existingPatient = patientDAO.getPatientById(patientId);

        if (existingPatient != null) {
            Person existingPerson = personDAO.getPersonById(patientId); // get existing person record
            Person partialUpdatedPerson = createPerson(partialUpdatedPatient); // create a person object
            partialUpdatedPerson.setPersonId(patientId); // set the ID of the person

            patientDAO.partialUpdatePatient(existingPatient, partialUpdatedPatient); // update the patient record
            personDAO.partialUpdatePerson(existingPerson, partialUpdatedPerson); // update the person record

            return Response.status(Response.Status.OK).entity("Patient with ID " + patientId + " was updated successfully").build();
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
        boolean removed = patientDAO.deletePatient(patientId); // delete the patient record
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
     * @return A response containing the list of patients that match the
     * specified criteria, or an appropriate error response if the search fails.
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
                throw new ResourceNotFoundException("No patients found with the given search criteria");
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

        // Validate if the patient exists
        Patient patient = patientDAO.getPatientById(patientId); // get the existing patient record
        if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }

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

        // Validate if the patient exists
        Patient patient = patientDAO.getPatientById(patientId); // get the existing patient record
        if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }

        List<Appointment> existingAppointments = appointmentDAO.getAppointmentByPatientId(patientId); // get patients appointments

        if (!existingAppointments.isEmpty()) {
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

        // Validate if the patient exists
        Patient patient = patientDAO.getPatientById(patientId); // get the existing patient record
        if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }

        List<Prescription> existingPrescriptions = prescriptionDAO.getPrescriptionByPatientId(patientId); // get patients prescriptions

        if (!existingPrescriptions.isEmpty()) {
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

        // Validate if the patient exists
        Patient patient = patientDAO.getPatientById(patientId); // get the existing patient record
        if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }

        List<Billing> existingBills = billingDAO.getBillByPatientId(patientId); // get patients bills

        if (!existingBills.isEmpty()) {
            return Response.ok().entity(existingBills).build();
        } else {
            throw new ResourceNotFoundException("Patient with ID " + patientId + " does not have any bills");
        }
    }

    /**
     *
     * Creates a Person object from a Patient object.
     *
     * @param patient The Patient object from which to create the Person object.
     * @return A Person object with the same first name, last name, address,
     * age, contact number, and gender as the Patient object.
     */
    private Person createPerson(Patient patient) {
        Person person = new Person();
        person.setFirstName(patient.getFirstName());
        person.setLastName(patient.getLastName());
        person.setAddress(patient.getAddress());
        person.setAge(patient.getAge());
        person.setContactNo(patient.getContactNo());
        person.setGender(patient.getGender());

        return person;
    }
}
