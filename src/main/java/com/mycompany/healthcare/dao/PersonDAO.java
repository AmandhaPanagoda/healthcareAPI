/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import com.mycompany.healthcare.helper.ObjectPatcherHelper;
import com.mycompany.healthcare.model.Person;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) for managing Person objects. This class provides
 * methods for retrieving, adding, updating, and deleting Person objects.
 *
 * @author Amandha
 */
public class PersonDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonDAO.class);
    private static final Map<Integer, Person> people = new HashMap<>();

    static {
        people.put(1, new Person(1, "Eric", "Anderson", 1124579548, "684 Delaware Avenue, SF", "M", 45));
        people.put(2, new Person(2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33));
        people.put(3, new Person(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60));
        people.put(4, new Person(4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25));
        people.put(5, new Person(5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35));
        people.put(6, new Person(6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45));
    }

    /**
     * Retrieves all people.
     *
     * @return A map of person IDs to Person objects.
     */
    public Map<Integer, Person> getAllPeople() {
        LOGGER.info("Retrieving all people");
        return people;
    }

    /**
     * Retrieves a person by ID.
     *
     * @param personId The ID of the person to retrieve.
     * @return The Person object with the given ID, or null if not found.
     */
    public Person getPersonById(int personId) {
        LOGGER.info("Retrieving the person with ID : " + personId);
        return people.get(personId);
    }

    /**
     * Adds a new person.
     *
     * @param person The person to add.
     * @return The ID of the newly added person.
     */
    public int addPerson(Person person) {
        LOGGER.info("Adding a new person");

        Helper<Person> helper = new Helper<>();
        int newPersonId = helper.getNextId(people); // Get new person ID

        person.setPersonId(newPersonId); // Set the new person ID
        people.put(newPersonId, person);
        LOGGER.info("New person with ID " + newPersonId + " was added to people list");
        return newPersonId;
    }

    /**
     * Updates an existing person.
     *
     * @param updatedPerson The updated Person object.
     */
    public void updatePerson(Person updatedPerson) {
        LOGGER.info("Updating person record");

        Person existingPerson = people.get(updatedPerson.getPersonId());
        if (existingPerson != null) {
            people.put(updatedPerson.getPersonId(), updatedPerson);
            LOGGER.info("Person ID : " + updatedPerson.getPersonId() + " was updated");
        } else {
            LOGGER.info("Person record was not updated! Person ID : " + updatedPerson.getPersonId() + " was not found");
        }
    }

    /**
     * Partially updates a Person object with the values from another Person
     * object. This method uses reflection to update the fields of the
     * existingPerson object with non-null values from the partialUpdatedPerson
     * object.
     *
     * @param existingPerson The existing Person object to be updated.
     * @param partialUpdatedPerson The Person object containing partial updates.
     */
    public void partialUpdatePerson(Person existingPerson, Person partialUpdatedPerson) {
        try {
            LOGGER.info("Updating the person record");
            ObjectPatcherHelper.objectPatcher(existingPerson, partialUpdatedPerson);
        } catch (IllegalAccessException e) {
            LOGGER.error("An error occured: " + e.getMessage());
        }
    }

    /**
     * Deletes a person by ID.
     *
     * @param personId The ID of the person to delete.
     * @return True if the person was successfully deleted, false otherwise.
     */
    public boolean deletePerson(int personId) {
        LOGGER.info("Deleting the person with ID: " + personId);
        return people.remove(personId) != null;
    }

    /**
     * Searches for people based on specified criteria.
     *
     * @param firstName The first name to match (case-insensitive). Pass null to
     * ignore this criteria.
     * @param lastName The last name to match (case-insensitive). Pass null to
     * ignore this criteria.
     * @param minAge The minimum age to match. Pass null to ignore this
     * criteria.
     * @param maxAge The maximum age to match. Pass null to ignore this
     * criteria.
     * @param gender The gender to match (case-insensitive). Pass null to ignore
     * this criteria.
     * @return A map of Person objects matching the specified criteria.
     */
    public List<Person> searchPeople(String firstName, String lastName, Integer minAge, Integer maxAge, String gender) {
        LOGGER.info("Searching for people with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + ", and gender: " + gender);

        List<Person> matchingPeople = new ArrayList<>();
        for (Person person : people.values()) {
            boolean matchFirstName = firstName == null || firstName.equalsIgnoreCase(person.getFirstName());
            boolean matchLastName = lastName == null || lastName.equalsIgnoreCase(person.getLastName());
            boolean matchAge = (minAge == null || person.getAge() >= minAge) && (maxAge == null || person.getAge() <= maxAge);
            boolean matchGender = gender == null || gender.equalsIgnoreCase(person.getGender());

            if (matchFirstName && matchLastName && matchAge && matchGender) {
                matchingPeople.add(person);
            }
        }
        return matchingPeople;
    }

}
