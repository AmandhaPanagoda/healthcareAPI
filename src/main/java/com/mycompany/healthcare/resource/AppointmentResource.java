/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.AppointmentDAO;
import com.mycompany.healthcare.dao.DoctorDAO;
import com.mycompany.healthcare.dao.PatientDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Appointment;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import java.util.List;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource class for managing appointments.
 *
 * @author Amandha
 */
@Path("/appointments")
public class AppointmentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentResource.class);
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    /**
     * Retrieves all appointments.
     *
     * @return A collection of all appointments.
     * @throws ResourceNotFoundException If no appointments are found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Appointment> getAllAppointments() {
        if (!appointmentDAO.getAllAppointments().isEmpty()) {
            LOGGER.info("Returning all appointments");
            return appointmentDAO.getAllAppointments().values();
        } else {
            throw new ResourceNotFoundException("No appointments were found");
        }
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param appointmentId The ID of the appointment to retrieve.
     * @return The appointment with the specified ID.
     * @throws ResourceNotFoundException If no appointment is found with the
     * given ID.
     */
    @GET
    @Path("/{appointmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment getAppointmentById(@PathParam("appointmentId") int appointmentId) {
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId); // get the appointment
        if (appointment != null) {
            LOGGER.info("Getting the appointment by ID: " + appointmentId);
            return appointment;
        } else {
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

    /**
     * Adds a new appointment.
     *
     * @param appointment The appointment to add.
     * @return A response indicating the success of the operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAppointment(Appointment appointment) {
        if (appointment == null) {
            LOGGER.error("Appointment object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Appointment cannot be null").build();
        }

        // Validate the appointment object
        String validationError = ValidationHelper.validate(appointment);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int patientId = appointment.getPatient().getPersonId();
        int doctorId = appointment.getDoctor().getPersonId();
        Patient patient = patientDAO.getPatientById(patientId); // get the existing patient record
        Doctor doctor = doctorDAO.getDoctorById(doctorId); // get the existing doctor record

        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor does not exist");
        } else if (patient == null) {
            throw new ResourceNotFoundException("Patient does not exist");
        }
        appointment.setDoctor(doctor); // set the existing doctor's details in the new appointment
        appointment.setPatient(patient); // set the existing patient's details in the new appointment

        int newAppointmentId = appointmentDAO.addAppointment(appointment); // add the new appointment
        if(newAppointmentId != -1) {
            return Response.status(Response.Status.CREATED).entity("New appointment with ID: " + newAppointmentId + " was added successfully").build();
        } 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occured when adding the appointment").build();
    }

    /**
     * Updates an existing appointment.
     *
     * @param appointmentId The ID of the appointment to update.
     * @param updatedAppointment The updated appointment object.
     * @return A response indicating the success of the operation.
     */
    @PUT
    @Path("/{appointmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAppointment(@PathParam("appointmentId") int appointmentId, Appointment updatedAppointment) {
        if (updatedAppointment == null) {
            LOGGER.error("Appointment object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Appointment cannot be null").build();
        }

        if (appointmentId != updatedAppointment.getAppointmentId()) { // IDs are immutable when updating
            throw new ModelIdMismatchException("IDs are immutable. The passed appointment IDs do not match");
        }
        
        // Validate the appointment object
        String validationError = ValidationHelper.validate(updatedAppointment);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }
        
        // Get the existing appointment
        Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId); 

        if (existingAppointment != null) { 
            int patientId = updatedAppointment.getPatient().getPersonId();
            int doctorId = updatedAppointment.getDoctor().getPersonId();
            Patient patient = patientDAO.getPatientById(patientId); // get the existing patient details
            Doctor doctor = doctorDAO.getDoctorById(doctorId); // get the existing doctor details

            if (doctor == null) {
                throw new ResourceNotFoundException("Doctor does not exist");
            } else if (patient == null) {
                throw new ResourceNotFoundException("Patient does not exist");
            }
            updatedAppointment.setDoctor(doctor); // set the doctor details in the appointment
            updatedAppointment.setPatient(patient); // set the patient deatils in the appointment

            appointmentDAO.updateAppointment(updatedAppointment); // update the existing appointment
            
            LOGGER.info("Appointment record was updated. Updated Appointment ID: " + appointmentId);
            return Response.status(Response.Status.OK).entity("Appointment with ID " + appointmentId + " was updated successfully").build();
        } else {
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

    /**
     * Deletes an appointment by its ID.
     *
     * @param appointmentId The ID of the appointment to delete.
     * @return A response indicating the success of the operation.
     */
    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId) {
        boolean removed = appointmentDAO.deleteAppointment(appointmentId); // delete the appointment
        if (removed) {
            return Response.status(Response.Status.OK).entity("Appointment with ID " + appointmentId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

    /**
     * Searches for appointments based on the provided criteria.
     *
     * @param patientFirstName The first name of the patient.
     * @param patientLastName The last name of the patient.
     * @param doctorFirstName The first name of the doctor.
     * @param doctorLastName The last name of the doctor.
     * @param fromDate The start date of the appointment.
     * @param toDate The end date of the appointment.
     * @param specialization The specialization of the doctor.
     * @return A response containing a list of appointments matching the
     * criteria.
     * @throws ResourceNotFoundException If no appointments are found with the
     * given criteria.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchAppointments(
            @QueryParam("patientFirstName") String patientFirstName,
            @QueryParam("patientLastName") String patientLastName,
            @QueryParam("doctorFirstName") String doctorFirstName,
            @QueryParam("doctorLastName") String doctorLastName,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("specialization") String specialization) {

        if ((patientFirstName == null || patientFirstName.isEmpty())
                && (patientLastName == null || patientLastName.isEmpty())
                && (doctorFirstName == null || doctorFirstName.isEmpty())
                && (doctorLastName == null || doctorLastName.isEmpty())
                && (fromDate == null || fromDate.isEmpty())
                && (toDate == null || toDate.isEmpty())
                && (specialization == null || specialization.isEmpty())) {
            return Response.ok(getAllAppointments()).build();
        }

        try {
            LOGGER.info("Searching for appointments in the given criteria. patientFirstName: " + patientFirstName
                    + " patientLastName: " + patientLastName
                    + " doctorFirstName: " + doctorFirstName
                    + " fromDateStr: " + fromDate
                    + " toDateStr: " + toDate
                    + " specialization: " + specialization);

            List<Appointment> matchingAppointments = appointmentDAO.searchAppointments(
                    patientFirstName, patientLastName, doctorFirstName, doctorLastName, fromDate, toDate, specialization);
            if (!matchingAppointments.isEmpty()) {
                return Response.ok(matchingAppointments).build();
            } else {
                throw new ResourceNotFoundException("No appointments found with the given search criteria");
            }
        } catch (BadRequestException e) {
            LOGGER.error("An error occured when processing the request. Message: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("An error occured when processing the request. Error: " + e.getMessage())
                    .build();
        }
    }

}
