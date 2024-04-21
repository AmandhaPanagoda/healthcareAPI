/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.DoctorDAO;
import com.mycompany.healthcare.dao.PersonDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import java.util.List;
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
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
@Path("/doctors")
public class DoctorResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorResource.class);

    private DoctorDAO doctorDAO = new DoctorDAO();
    private PersonDAO personDAO = new PersonDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Doctor> getAllDoctors() {
        if (doctorDAO.getAllDoctors() != null) {
            return doctorDAO.getAllDoctors();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

    @GET
    @Path("/{doctorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getDoctorById(@PathParam("doctorId") int doctorId) {
        LOGGER.info("Getting the doctor by ID: " + doctorId);
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        if (doctor != null) {
            return doctor;
        } else {
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDoctor(Doctor doctor) {
        LOGGER.info("Adding a new doctor");

        // Validate the doctor object
        String validationError = ValidationHelper.validate(doctor);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }
        int newDoctorId = doctorDAO.addDoctor(doctor);
        personDAO.addPerson(doctor); // add the doctor to the person list

        return Response.status(Response.Status.CREATED).entity("New doctor with ID: " + newDoctorId + " was added successfully").build();
    }

    @PUT
    @Path("/{doctorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDoctor(@PathParam("doctorId") int doctorId, Doctor updatedDoctor) {
        if (doctorId != updatedDoctor.getDoctorId()) {
            LOGGER.info("URL parameter doctor ID and the passed doctor ID do not match");

            return Response.status(Response.Status.OK).entity("The passed doctor IDs do not match").build();
        }
        LOGGER.info("Updating doctor with ID: " + doctorId);
        Doctor existingDoctor = doctorDAO.getDoctorById(doctorId);

        if (existingDoctor == null) {
            LOGGER.error("Doctor ID" + doctorId + " was not found");

            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found");
        } else if (existingDoctor.getPersonId() != updatedDoctor.getPersonId()) {
            String message = "Person ID of the doctor cannot be updated. Existing Person ID: " + existingDoctor.getPersonId() + ". Passed Person ID: " + updatedDoctor.getPersonId();
            LOGGER.info(message);

            return Response.status(Response.Status.OK).entity(message).build();
        } else {
            String validationError = ValidationHelper.validate(updatedDoctor);
            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
            }

            updatedDoctor.setDoctorId(doctorId);
            doctorDAO.updateDoctor(updatedDoctor);
            personDAO.updatePerson(updatedDoctor); //update the person record
            LOGGER.info("Doctor record was updated. Updated Doctor ID: " + doctorId);

            return Response.status(Response.Status.OK).entity("Doctor with ID " + doctorId + " was updated successfully").build();
        }
    }

    @DELETE
    @Path("/{doctorId}")
    public Response deleteDoctor(@PathParam("doctorId") int doctorId) {
        boolean removed = doctorDAO.deleteDoctor(doctorId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Doctor with ID " + doctorId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found");
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDoctors(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("minAge") Integer minAge,
            @QueryParam("maxAge") Integer maxAge,
            @QueryParam("gender") String gender,
            @QueryParam("specialization") String specialization) {

        LOGGER.info("Searching for people with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + ", gender: " + gender + " and specialization: " + specialization);

        if ((firstName == null || firstName.isEmpty())
                && (lastName == null || lastName.isEmpty())
                && (specialization == null || specialization.isEmpty())
                && (minAge == null)
                && (maxAge == null)
                && (gender == null || gender.isEmpty())) {
            return Response.ok(getAllDoctors()).build();
        }

        try {
            List<Doctor> matchingDoctors = doctorDAO.searchDoctors(firstName, lastName, minAge, maxAge, gender, specialization);
            if (!matchingDoctors.isEmpty()) {
                return Response.ok(matchingDoctors).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No doctors found with the given search criteria")
                        .build();
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
