/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.AppointmentDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import java.util.List;
import com.mycompany.healthcare.model.Appointment;
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
 *
 * @author Amandha
 */
@Path("/appointments")
public class AppointmentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentResource.class);
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Appointment> getAllAppointments() {
        LOGGER.info("Getting all appointments");
        if (appointmentDAO.getAllAppointments() != null) {
            return appointmentDAO.getAllAppointments().values();
        } else {
            LOGGER.info("No appointments were found");
            throw new ResourceNotFoundException("No records were found");
        }
    }

    @GET
    @Path("/{appointmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment getAppointmentById(@PathParam("appointmentId") int appointmentId) {
        LOGGER.info("Getting the appointment by ID: " + appointmentId);
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment != null) {
            return appointment;
        } else {
            LOGGER.info("Appointment ID does not exist");
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAppointment(Appointment appointment) {
        LOGGER.info("Adding a new appointment");

        // Validate the appointment object
        String validationError = ValidationHelper.validate(appointment);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newAppointmentId = appointmentDAO.addAppointment(appointment);
        return Response.status(Response.Status.CREATED).entity("New appointment with ID: " + newAppointmentId + " was added successfully").build();
    }

    @PUT
    @Path("/{appointmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAppointment(@PathParam("appointmentId") int appointmentId, Appointment updatedAppointment) {

        // Validate the appointment object
        String validationError = ValidationHelper.validate(updatedAppointment);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        if (appointmentId != updatedAppointment.getAppointmentId()) { // IDs are immutable when updating
            LOGGER.info("URL parameter appointment ID and the passed appointment ID do not match");
            return Response.status(Response.Status.OK).entity("IDs are immutable. The passed appointment IDs do not match").build();
        }

        LOGGER.info("Updating appointment with ID: " + appointmentId);
        Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId);

        if (existingAppointment != null) { //check if an appointment exists
            appointmentDAO.updateAppointment(updatedAppointment); // update the existing appointment
            LOGGER.info("Appointment record was updated. Updated Appointment ID: " + appointmentId);
            return Response.status(Response.Status.OK).entity("Appointment with ID " + appointmentId + " was updated successfully").build();
        } else {
            LOGGER.error("Appointment ID: " + appointmentId + " was not found");
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

    @DELETE
    @Path("/{appointmentId}")
    public Response deleteAppointment(@PathParam("appointmentId") int appointmentId) {
        LOGGER.info("Deleting appointment with ID: " + appointmentId);
        boolean removed = appointmentDAO.deleteAppointment(appointmentId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Appointment with ID " + appointmentId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Appointment with ID " + appointmentId + " was not found");
        }
    }

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

        LOGGER.info("Searching for appointments...");

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
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No appointments found with the given search criteria")
                        .build();
            }
        } catch (BadRequestException e) {
            LOGGER.error("An error occured when processing the request. Message: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("An error occured when processing the request. Error: " + e.getMessage())
                    .build();
        }
    }

}
