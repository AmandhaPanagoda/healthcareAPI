/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.DoctorDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.dao.PrescriptionDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
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
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * RESTful web service resource class for managing Prescription objects.
 * Provides endpoints for retrieving, adding, updating, and deleting prescriptions.
 * 
 * @author Amandha
 */
@Path("/prescriptions")
public class PrescriptionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionResource.class);
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    /**
     * Retrieves all prescriptions.
     *
     * @return Collection of prescriptions.
     * @throws ResourceNotFoundException if no prescriptions are found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Prescription> getAllPrescriptions() {
        if (!prescriptionDAO.getAllPrescriptions().isEmpty()) {
            LOGGER.info("Fetching all prescriptions");
            return prescriptionDAO.getAllPrescriptions().values();
        } else {
            throw new ResourceNotFoundException("No prescriptions were found");
        }
    }

    /**
     * Retrieves a prescription by ID.
     *
     * @param prescriptionId The ID of the prescription to retrieve.
     * @return The prescription with the specified ID.
     * @throws ResourceNotFoundException if the prescription with the specified
     * ID is not found.
     */
    @GET
    @Path("/{prescriptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Prescription getPrescriptionById(@PathParam("prescriptionId") int prescriptionId) {
        Prescription prescription = prescriptionDAO.getPrescriptionById(prescriptionId); // get the prescription
        if (prescription != null) {
            LOGGER.info("Getting the prescription by ID: " + prescriptionId);
            return prescription;
        } else {
            throw new ResourceNotFoundException("Prescription with ID " + prescriptionId + " was not found");
        }
    }

    /**
     * Adds a new prescription.
     *
     * @param prescription The prescription to add.
     * @return Response indicating the success of the operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPrescription(Prescription prescription) {
        if (prescription == null) {
            LOGGER.error("Prescription object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Prescription cannot be null").build();
        }

        // Validate the prescription object
        String validationError = ValidationHelper.validate(prescription);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int patientId = prescription.getPrescribedFor().getPersonId();
        int doctorId = prescription.getPrescribedBy().getPersonId();
        Patient patient = patientDAO.getPatientById(patientId); // get the patient details
        Doctor doctor = doctorDAO.getDoctorById(doctorId); // get the doctor details

        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor does not exist");
        } else if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }
        prescription.setPrescribedBy(doctor); // set the doctor details in the prescription
        prescription.setPrescribedFor(patient); // set the patient details in the prescription

        int newPrescriptionId = prescriptionDAO.addPrescription(prescription); // add the new prescription
        if (newPrescriptionId != -1) {
            return Response.status(Response.Status.CREATED).entity("New prescription with ID: " + newPrescriptionId + " was added successfully").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occured when adding the prescription").build();
    }

    /**
     * Updates a prescription.
     *
     * @param prescriptionId The ID of the prescription to update.
     * @param updatedPrescription The updated prescription information.
     * @return Response indicating the success of the operation.
     */
    @PUT
    @Path("/{prescriptionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePrescription(@PathParam("prescriptionId") int prescriptionId, Prescription updatedPrescription) {
        if (updatedPrescription == null) {
            LOGGER.error("Prescription object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Prescription cannot be null").build();
        }

        if (prescriptionId != updatedPrescription.getPrescriptionId()) {
            throw new ModelIdMismatchException("The passed prescription IDs do not match"); // IDs are immutable and generated
        }

        // Validate the prescription object
        String validationError = ValidationHelper.validate(updatedPrescription);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }
        
        // Get the existing prescription
        Prescription existingPrescription = prescriptionDAO.getPrescriptionById(prescriptionId);

        if (existingPrescription != null) {
            int patientId = updatedPrescription.getPrescribedFor().getPersonId();
            int doctorId = updatedPrescription.getPrescribedBy().getPersonId();
            Patient patient = patientDAO.getPatientById(patientId); // get the existing patient details
            Doctor doctor = doctorDAO.getDoctorById(doctorId); // get the existing doctor details

            if (doctor == null) {
                throw new ResourceNotFoundException("Doctor does not exist");
            } else if (patient == null) {
                throw new ResourceNotFoundException("Patient does not exist");
            }
            updatedPrescription.setPrescribedBy(doctor); // set the doctor details in the prescription
            updatedPrescription.setPrescribedFor(patient); // set the patient details in the prescription

            prescriptionDAO.updatePrescription(updatedPrescription); // update the prescription
            
            LOGGER.info("Prescription record was updated. Updated Prescription ID: " + prescriptionId);
            return Response.status(Response.Status.OK).entity("Prescription with ID " + prescriptionId + " was updated successfully").build();
        } else {
            LOGGER.error("Prescription ID " + prescriptionId + " was not found");
            throw new ResourceNotFoundException("Prescription with ID " + prescriptionId + " was not found");
        }
    }

    /**
     * Deletes a prescription.
     *
     * @param prescriptionId The ID of the prescription to delete.
     * @return Response indicating the success of the operation.
     * @throws ResourceNotFoundException if the prescription with the specified
     * ID is not found.
     */
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

    /**
     * Searches for prescriptions based on the provided criteria. If no criteria
     * are provided, returns all prescriptions.
     *
     * @param patientFirstName The first name of the patient to search for.
     * @param patientLastName The last name of the patient to search for.
     * @param doctorFirstName The first name of the doctor to search for.
     * @param doctorLastName The last name of the doctor to search for.
     * @param fromDate The start date of the prescription (format: dd-MM-yyyy).
     * @param toDate The end date of the prescription (format: dd-MM-yyyy).
     * @return A response containing a list of prescriptions matching the
     * criteria.
     * @throws ResourceNotFoundException If no prescriptions are found with the
     * given criteria.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPrescriptionss(
            @QueryParam("patientFirstName") String patientFirstName,
            @QueryParam("patientLastName") String patientLastName,
            @QueryParam("doctorFirstName") String doctorFirstName,
            @QueryParam("doctorLastName") String doctorLastName,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) {

        if ((patientFirstName == null || patientFirstName.isEmpty())
                && (patientLastName == null || patientLastName.isEmpty())
                && (doctorFirstName == null || doctorFirstName.isEmpty())
                && (doctorLastName == null || doctorLastName.isEmpty())
                && (fromDate == null || fromDate.isEmpty())
                && (toDate == null || toDate.isEmpty())) {
            return Response.ok(getAllPrescriptions()).build();
        }

        try {
            LOGGER.info("Searching for prescriptions in the given criteria. patientFirstName: " + patientFirstName
                    + " patientLastName: " + patientLastName
                    + " doctorFirstName: " + doctorFirstName
                    + " fromDateStr: " + fromDate
                    + " toDateStr: " + toDate);

            List<Prescription> matchingPrescriptions = prescriptionDAO.searchPrescriptions(
                    patientFirstName, patientLastName, doctorFirstName, doctorLastName, fromDate, toDate);
            if (!matchingPrescriptions.isEmpty()) {
                return Response.ok(matchingPrescriptions).build();
            } else {
                throw new ResourceNotFoundException("No prescriptions were found with the given search criteria");
            }
        } catch (BadRequestException e) {
            LOGGER.error("An error occured when processing the request. Message: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("An error occured when processing the request. Error: " + e.getMessage())
                    .build();
        }
    }
}
