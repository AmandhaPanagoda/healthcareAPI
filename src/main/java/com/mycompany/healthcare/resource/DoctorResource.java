/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.AppointmentDAO;
import com.mycompany.healthcare.dao.DoctorDAO;
import com.mycompany.healthcare.dao.PersonDAO;
import com.mycompany.healthcare.dao.PrescriptionDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Appointment;
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
import com.mycompany.healthcare.model.Prescription;
import java.util.Collection;
import javax.ws.rs.PATCH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class for managing Doctor entities. This class provides RESTful
 * endpoints for CRUD operations on doctors, as well as methods for searching
 * doctors based on various criteria. Additionally, it provides methods for
 * retrieving a doctor's appointments and prescriptions.
 *
 * @author Amandha
 */
@Path("/doctors")
public class DoctorResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorResource.class);

    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PersonDAO personDAO = new PersonDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    /**
     * Retrieves all doctors.
     *
     * @return A collection of all doctors.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Doctor> getAllDoctors() {
        LOGGER.info("Fetching all doctor records");
        if (!doctorDAO.getAllDoctors().isEmpty()) {
            return doctorDAO.getAllDoctors().values();
        } else {
            LOGGER.info("No records were found");
            throw new ResourceNotFoundException("No records of doctors were found");
        }
    }

    /**
     * Retrieves a doctor by ID.
     *
     * @param doctorId The ID of the doctor to retrieve.
     * @return The doctor with the specified ID.
     */
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

    /**
     * Adds a new doctor.
     *
     * @param doctor The doctor to add.
     * @return A response indicating the success of the operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new BadRequestException("Doctor record cannot be null");
        }
        LOGGER.info("Adding a new doctor");

        // Validate the doctor object
        String validationError = ValidationHelper.validate(doctor);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        Person person = createPerson(doctor);
        int newDoctorId = personDAO.addPerson(person); // add the doctor to the person list
        doctor.setPersonId(newDoctorId);
        doctorDAO.addDoctor(doctor); // add the new doctor and get the new doctor id

        return Response.status(Response.Status.CREATED).entity("New doctor with ID: " + newDoctorId + " was added successfully").build();
    }

    /**
     * Updates an existing doctor.
     *
     * @param doctorId The ID of the doctor to update.
     * @param updatedDoctor The updated doctor information.
     * @return A response indicating the success of the operation.
     */
    @PUT
    @Path("/{doctorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDoctor(@PathParam("doctorId") int doctorId, Doctor updatedDoctor) {
        if (doctorId != updatedDoctor.getPersonId()) {
            LOGGER.info("URL parameter doctor ID and the passed doctor ID do not match");
            return Response.status(Response.Status.CONFLICT).entity("The passed doctor IDs do not match").build();
        }

        LOGGER.info("Updating doctor with ID: " + doctorId);
        Doctor existingDoctor = doctorDAO.getDoctorById(doctorId);

        if (existingDoctor == null) {
            LOGGER.error("Doctor ID" + doctorId + " was not found");
            throw new ResourceNotFoundException("Error in updating! Doctor with ID " + doctorId + " was not found");

        } else {
            String validationError = ValidationHelper.validate(updatedDoctor);
            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
            }

            doctorDAO.updateDoctor(updatedDoctor); // update the doctor record
            
            Person person = createPerson(updatedDoctor);
            person.setPersonId(updatedDoctor.getPersonId());
            personDAO.updatePerson(person); // update the person record
            
            LOGGER.info("Doctor record was updated. Updated Doctor ID: " + doctorId);

            return Response.status(Response.Status.OK).entity("Doctor with ID " + doctorId + " was updated successfully").build();
        }
    }

    /**
     * Partially updates an existing doctor record by applying non-null fields
     * from the partial updated doctor object.
     *
     * @param doctorId The ID of the doctor to update.
     * @param partialUpdatedDoctor The partial updated doctor object containing
     * the new values.
     * @return A response indicating the status of the update operation.
     */
    @PATCH
    @Path("/{doctorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response partialUpdateDoctor(@PathParam("doctorId") int doctorId, Doctor partialUpdatedDoctor) {
        if (partialUpdatedDoctor.getPersonId() != 0 && doctorId != partialUpdatedDoctor.getPersonId()) {
            LOGGER.info("URL parameter doctor ID and the passed doctor ID do not match");
            throw new ModelIdMismatchException("The passed doctor IDs do not match");
        }

        Doctor existingDoctor = doctorDAO.getDoctorById(doctorId);
        Person existingPerson = personDAO.getPersonById(doctorId);
        if (existingDoctor != null) {
            doctorDAO.partialUpdateDoctor(existingDoctor, partialUpdatedDoctor);
            personDAO.partialUpdatePerson(existingPerson, partialUpdatedDoctor);
            
            return Response.status(Response.Status.OK).entity("Doctor with ID " + doctorId + " was updated successfully").build();
        } else {
            LOGGER.error("Doctor ID" + doctorId + " was not found");
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found");
        }
    }

    /**
     * Deletes a doctor by ID.
     *
     * @param doctorId The ID of the doctor to delete.
     * @return A response indicating the success of the operation.
     */
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

    /**
     * Searches for doctors based on specified criteria.
     *
     * @param firstName The first name of the doctor.
     * @param lastName The last name of the doctor.
     * @param minAge The minimum age of the doctor.
     * @param maxAge The maximum age of the doctor.
     * @param gender The gender of the doctor.
     * @param specialization The specialization of the doctor.
     * @return A response containing the matching doctors.
     */
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

        LOGGER.info("Searching for doctors with first name: " + firstName + ", last name: " + lastName
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
                throw new ResourceNotFoundException("No doctors found with the given search criteria");
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Retrieves all appointments of a doctor based on the doctor's ID.
     *
     * @param doctorId The ID of the doctor.
     * @return A Response containing the list of appointments in JSON format.
     * @throws ResourceNotFoundException if the doctor does not have any
     * appointments.
     */
    @GET
    @Path("/{doctorId}/appointments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDoctorAppointments(@PathParam("doctorId") int doctorId) {
        LOGGER.info("Searching for appointments of doctor with ID: " + doctorId);
        List<Appointment> existingAppointments = appointmentDAO.getAppointmentByDoctorId(doctorId); // get doctors appointments

        if (!existingAppointments.isEmpty()) {
            return Response.ok().entity(existingAppointments).build();
        } else {
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " does not have any appointments");
        }
    }

    /**
     * Retrieves all prescriptions of a doctor based on the doctor's ID.
     *
     * @param doctorId The ID of the doctor.
     * @return A Response containing the list of prescriptions in JSON format.
     * @throws ResourceNotFoundException if the doctor does not have any
     * prescriptions.
     */
    @GET
    @Path("/{doctorId}/prescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDoctorPrescriptions(@PathParam("doctorId") int doctorId) {
        LOGGER.info("Searching for prescriptions of doctor with ID: " + doctorId);
        List<Prescription> existingPrescriptions = prescriptionDAO.getPrescriptionByDoctorId(doctorId); // get prescriptions created by the doctor

        if (!existingPrescriptions.isEmpty()) {
            return Response.ok().entity(existingPrescriptions).build();
        } else {
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " has not created any prescriptions");
        }
    }

    private Person createPerson(Doctor doctor) {
        Person person = new Person();
        person.setFirstName(doctor.getFirstName());
        person.setLastName(doctor.getLastName());
        person.setAddress(doctor.getAddress());
        person.setAge(doctor.getAge());
        person.setContactNo(doctor.getContactNo());
        person.setGender(doctor.getGender());
        
        return person;
    }

}
