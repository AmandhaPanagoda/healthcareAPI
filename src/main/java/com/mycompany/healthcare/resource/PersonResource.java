/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.DoctorDAO;
import com.mycompany.healthcare.dao.PatientDAO;
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
import com.mycompany.healthcare.model.Person;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mycompany.healthcare.dao.PersonDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import javax.ws.rs.PATCH;
import org.modelmapper.ModelMapper;

/**
 * RESTful web service resource for managing people. This resource provides
 * endpoints for retrieving, adding, updating, and deleting people records.
 *
 * @author Amandha
 */
@Path("/people")
public class PersonResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);
    private final PersonDAO personDAO = new PersonDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    /**
     * Retrieves all people records.
     *
     * @return A collection of Person objects.
     * @throws ResourceNotFoundException If no records are found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Person> getAllPeople() {
        if (!personDAO.getAllPeople().isEmpty()) {
            LOGGER.info("Fetching all person records");
            return personDAO.getAllPeople().values();
        } else {
            throw new ResourceNotFoundException("No people records were found");
        }
    }

    /**
     * Retrieves a person record by ID.
     *
     * @param personId The ID of the person to retrieve.
     * @return The Person object with the specified ID.
     * @throws ResourceNotFoundException If the person with the specified ID is
     * not found.
     */
    @GET
    @Path("/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPersonById(@PathParam("personId") int personId) {
        Person person = personDAO.getPersonById(personId);
        if (person != null) {
            LOGGER.info("Getting the person by ID: " + personId);
            return person;
        } else {
            throw new ResourceNotFoundException("Person with ID " + personId + " was not found");
        }
    }

    /**
     * Adds a new person record.
     *
     * @param person The Person object to add.
     * @return A Response object with status 201 (Created) and a message
     * indicating the new person ID, if successful. A Response object with
     * status 400 (Bad Request) and an error message if the person object is not
     * valid.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPerson(Person person) {
        if (person == null) {
            throw new BadRequestException("Person record cannot be null");
        }

        // Validate the person object
        String validationError = ValidationHelper.validate(person);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPersonId = personDAO.addPerson(person); // add the new person and get new person id

        if (newPersonId != -1) {
            return Response.status(Response.Status.CREATED).entity("New person with ID: " + newPersonId + " was added successfully").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occured when adding the person").build();
    }

    /**
     * Updates an existing person record.
     *
     * @param personId The ID of the person to update.
     * @param updatedPerson The updated Person object.
     * @return A Response object with status 200 (OK) and a message indicating
     * the successful update, if successful. A Response object with status 404
     * (Not Found) if the person with the specified ID is not found. A Response
     * object with status 409 (Conflict) if the person IDs in the URL and the
     * passed person object do not match.
     */
    @PUT
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("personId") int personId, Person updatedPerson) {
        if (updatedPerson == null) {
            LOGGER.error("Person object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Person cannot be null").build();
        }

        if (personId != updatedPerson.getPersonId()) {
            throw new ModelIdMismatchException("The passed person IDs do not match");
        }

        // Validate the person object
        String validationError = ValidationHelper.validate(updatedPerson);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        Person existingPerson = personDAO.getPersonById(personId); // get existing person record

        if (existingPerson != null) {
            Doctor doctor = doctorDAO.getDoctorById(personId); 
            Patient patient = patientDAO.getPatientById(personId);
            
            ModelMapper modelMapper = new ModelMapper(); // create a mapper
            if (patient != null) {
                Patient partialUpdatedPatient = modelMapper.map(updatedPerson, Patient.class);
                patientDAO.partialUpdatePatient(patient,partialUpdatedPatient); // update the patient record
            }
            if (doctor != null) {
                Doctor partialUpdatedDoctor = modelMapper.map(updatedPerson, Doctor.class);
                doctorDAO.partialUpdateDoctor(doctor, partialUpdatedDoctor); // update the doctor record
            }
            
            personDAO.updatePerson(updatedPerson); // update the person record

            LOGGER.info("Person record was updated. Updated Person ID: " + personId);
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was updated successfully").build();
        } else {
            throw new ResourceNotFoundException("Person with ID " + personId + " was not found");
        }
    }

    /**
     * Partially updates a Person object with the values from another Person
     * object.
     *
     * @param personId The ID of the person to update.
     * @param partialUpdatedPerson The Person object containing partial updates.
     * @return A Response indicating the success of the update operation.
     */
    @PATCH
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response partialUpdatePerson(@PathParam("personId") int personId, Person partialUpdatedPerson) {
        if (partialUpdatedPerson == null) {
            LOGGER.error("Person object cannot be null");
            return Response.status(Response.Status.BAD_REQUEST).entity("Person cannot be null").build();
        }

        if (personId != partialUpdatedPerson.getPersonId()) {
            throw new ModelIdMismatchException("The passed person IDs do not match");
        }

        // Get the existing patient record
        Person existingPerson = personDAO.getPersonById(personId);

        if (existingPerson != null) {
            Doctor doctor = doctorDAO.getDoctorById(personId);
            Patient patient = patientDAO.getPatientById(personId);

            ModelMapper modelMapper = new ModelMapper(); // create a mapper
            personDAO.partialUpdatePerson(existingPerson, partialUpdatedPerson); // update the person record
            if (patient != null) {
                Patient partialUpdatedPatient = modelMapper.map(partialUpdatedPerson, Patient.class);
                patientDAO.partialUpdatePatient(patient,partialUpdatedPatient); // update the patient record
            }
            if (doctor != null) {
                Doctor partialUpdatedDoctor = modelMapper.map(partialUpdatedPerson, Doctor.class);
                doctorDAO.partialUpdateDoctor(doctor,partialUpdatedDoctor); // update the doctor record
            }
            
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was updated successfully").build();
        } else {
            throw new ResourceNotFoundException("Person with ID " + personId + " was not found");
        }
    }

    /**
     * Deletes a person record.
     *
     * @param personId The ID of the person to delete.
     * @return A Response object with status 200 (OK) and a message indicating
     * the successful deletion, if successful. A Response object with status 404
     * (Not Found) if the person with the specified ID is not found.
     */
    @DELETE
    @Path("/{personId}")
    public Response deletePerson(@PathParam("personId") int personId) {
        patientDAO.deletePatient(personId); // delete the patient record from patients if it exists
        doctorDAO.deleteDoctor(personId); // delete the doctor record from doctors if it exists
        
        boolean removed = personDAO.deletePerson(personId);

        if (removed) {
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Person with ID " + personId + " was not found");
        }
    }

    /**
     * Searches for people based on specified criteria.
     *
     * @param firstName The first name of the person to search for.
     * @param lastName The last name of the person to search for.
     * @param minAge The minimum age of the person to search for.
     * @param maxAge The maximum age of the person to search for.
     * @param gender The gender of the person to search for.
     * @return A Response object with status 200 (OK) and a list of matching
     * Person objects, if any are found. A Response object with status 404 (Not
     * Found) if no matching people are found. A Response object with status 400
     * (Bad Request) if there is an error in the search criteria.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPeople(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("minAge") Integer minAge,
            @QueryParam("maxAge") Integer maxAge,
            @QueryParam("gender") String gender) {

        LOGGER.info("Searching for people with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + ", and gender: " + gender);

        if ((firstName == null || firstName.isEmpty())
                && (lastName == null || lastName.isEmpty())
                && (minAge == null)
                && (maxAge == null)
                && (gender == null || gender.isEmpty())) {
            return Response.status(Response.Status.OK)
                    .entity(getAllPeople())
                    .build();
        }

        try {
            List<Person> matchingPeople = personDAO.searchPeople(firstName, lastName, minAge, maxAge, gender);
            if (!matchingPeople.isEmpty()) {
                return Response.ok(matchingPeople).build();
            } else {
                throw new ResourceNotFoundException("No people were found with the given search criteria");
            }
        } catch (BadRequestException e) {
            LOGGER.error("An error occured : " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
