/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.PersonDAO;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.healthcare.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
@Path("/people")
public class PersonResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);
    private final PersonDAO personDAO = new PersonDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getAllPeople() {
        if (personDAO.getAllPeople() != null) {
            return personDAO.getAllPeople();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPerson(Person person) {
        LOGGER.info("Adding a new person");

        // Validate the person object
        String validationError = ValidationHelper.validate(person);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newPersonId = personDAO.addPerson(person);
        return Response.status(Response.Status.CREATED).entity("New person with ID: " + newPersonId + " was added successfully").build();
    }

    @PUT
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("personId") int personId, Person updatedPerson) {
        if (personId != updatedPerson.getPersonId()) {
            LOGGER.info("URL parameter person ID and the passed person ID do not match");
            return Response.status(Response.Status.OK).entity("The passed person IDs do not match").build();
        }
        LOGGER.info("Updating person with ID: " + personId);
        Person existingPerson = personDAO.getPersonById(personId);

        if (existingPerson != null) {
            updatedPerson.setPersonId(personId);
            personDAO.updatePerson(updatedPerson);
            LOGGER.info("Person record was updated. Updated Person ID: " + personId);
            return Response.status(Response.Status.OK).entity("Person with ID " + personId + " was updated successfully").build();
        } else {
            LOGGER.error("Person ID" + personId + " was not found");
            throw new ResourceNotFoundException("Person with ID " + personId + " was not found");
        }
    }

    @PATCH
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response patchPerson(@PathParam("personId") int personId, Person updatedPerson) {
        if (personId != updatedPerson.getPersonId()) {
            return Response.status(Response.Status.NOT_MODIFIED).entity("Person IDs do not match").build();
        }
        Person existingPerson = personDAO.getPersonById(personId);

        if (existingPerson != null) {
            updatedPerson.setPersonId(personId);
            personDAO.patchPerson(updatedPerson);
            return Response.status(Response.Status.OK).entity(updatedPerson).build();
        } else {
            throw new ResourceNotFoundException("Person with ID " + personId + " not found");
        }
    }

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
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("At least one search criteria is required")
                    .build();
        }

        try {
            List<Person> matchingPeople = personDAO.searchPeople(firstName, lastName, minAge, maxAge, gender);
            if (!matchingPeople.isEmpty()) {
                return Response.ok(matchingPeople).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No people found with the given search criteria")
                        .build();
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
