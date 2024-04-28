/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

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
import javax.ws.rs.PATCH;

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

    /**
     * Retrieves all people records.
     *
     * @param sortMethod
     * @return A collection of Person objects.
     * @throws ResourceNotFoundException If no records are found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Person> getAllPeople(@QueryParam("sort") String sortMethod) {
        LOGGER.info("Fetching all person records");
        if (!personDAO.getAllPeople().isEmpty()) {
            return personDAO.getAllPeople().values();
        } else {
            LOGGER.info("No records were found");
            throw new ResourceNotFoundException("No records were found");
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
        LOGGER.info("Getting the person by ID: " + personId);
        Person person = personDAO.getPersonById(personId);
        if (person != null) {
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
        LOGGER.info("Adding a new person");

        // Validate the person object
        String validationError = ValidationHelper.validate(person);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPersonId = personDAO.addPerson(person); // add the new person and get new person id
        return Response.status(Response.Status.CREATED).entity("New person with ID: " + newPersonId + " was added successfully").build();
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
        if (personId != updatedPerson.getPersonId()) {
            LOGGER.info("URL parameter person ID and the passed person ID do not match");
            throw new ModelIdMismatchException("The passed person IDs do not match");
        }
        LOGGER.info("Updating person with ID: " + personId);
        Person existingPerson = personDAO.getPersonById(personId);
        
         // Validate the person object
        String validationError = ValidationHelper.validate(updatedPerson);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }
        
        if (existingPerson != null) {
            personDAO.updatePerson(updatedPerson);
            LOGGER.info("Person record was updated. Updated Person ID: " + personId);
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was updated successfully").build();
        } else {
            LOGGER.error("Person ID" + personId + " was not found");
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
        if (partialUpdatedPerson.getPersonId() != 0 && personId != partialUpdatedPerson.getPersonId()) {
            LOGGER.info("URL parameter person ID and the passed person ID do not match");
            throw new ModelIdMismatchException("The passed person IDs do not match");
        }

        Person existingPerson = personDAO.getPersonById(personId);
        if (existingPerson != null) {
            personDAO.partialUpdatePerson(existingPerson, partialUpdatedPerson);
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was updated successfully").build();
        } else {
            LOGGER.error("Person ID" + personId + " was not found");
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
                    .entity(getAllPeople(null))
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
