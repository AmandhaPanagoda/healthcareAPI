/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.healthcare.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

/**
 *
 * @author Amandha
 */
public class PersonDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonDAO.class);
    private static final List<Person> people = new ArrayList<>();

    static {
        people.add(new Person(1, "Eric", "Anderson", 1234548548, "684 Delaware Avenue, SF", "M", 45));
        people.add(new Person(2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33));
        people.add(new Person(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60));
        people.add(new Person(4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25));
        people.add(new Person(5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35));
        people.add(new Person(6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45));
    }

    public List<Person> getAllPeople() {
        LOGGER.info("Retrieving all the people");
        return people;
    }

    public Person getPersonById(int personId) {
        LOGGER.info("Retrieving the person with ID : " + personId);
        for (Person person : people) {
            if (person.getPersonId() == personId) {
                return person;
            }
        }
        LOGGER.info("Person with ID : " + personId + " was not found");
        return null;
    }

    public int addPerson(Person person) {
        LOGGER.info("Adding a new person");

        Helper<Person> helper = new Helper<>();
        int newPersonId = helper.getNextId(people, Person::getPersonId); //get the next person ID

        person.setPersonId(newPersonId); // set the new person ID
        people.add(person);
        return newPersonId;
    }

    public void updatePerson(Person updatedPerson) {
        LOGGER.info("Updating person record");
        for (int i = 0; i < people.size(); i++) {
            Person person = people.get(i);
            int updatedPersonId = updatedPerson.getPersonId();
            if (person.getPersonId() == updatedPersonId) {
                people.set(i, updatedPerson);
                LOGGER.info("Person ID : " + updatedPersonId + " was updated");
                return;
            }
            LOGGER.info("Person record was not updated! Person ID : " + updatedPersonId + " was not found");
        }
    }

    public void patchPerson(Person updatedPerson) {
        for (int i = 0; i < people.size(); i++) {
            Person person = people.get(i);
            if (person.getPersonId() == updatedPerson.getPersonId()) {
                if (updatedPerson.getFirstName() != null) {
                    person.setFirstName(updatedPerson.getFirstName());
                }
                if (updatedPerson.getLastName() != null) {
                    person.setLastName(updatedPerson.getLastName());
                }
                if (updatedPerson.getAddress() != null) {
                    person.setAddress(updatedPerson.getAddress());
                }
                if (updatedPerson.getAge() >= 0) {
                    person.setAge(updatedPerson.getAge());
                }
                if (updatedPerson.getContactNo() > 0) {
                    person.setContactNo(updatedPerson.getContactNo());
                }
                if (updatedPerson.getGender() != null) {
                    person.setGender(updatedPerson.getGender());
                }

                // Update the person in the list
                people.set(i, person);
                break;
            }
        }
    }

    public boolean deletePerson(int personId) {
        LOGGER.info("Deleting the person with ID: " + personId);
        boolean removed = people.removeIf(person -> {
            if (person.getPersonId() == personId) {
                LOGGER.info("Person record with Person ID: " + personId + " was deleted");
                return true;
            }
            LOGGER.info("Person record with Person ID: " + personId + " was not found found");
            return false;
        });
        return removed;
    }

    public List<Person> searchPeople(String firstName, String lastName, Integer minAge, Integer maxAge, String gender) {
        LOGGER.info("Searching for people with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + ", and gender: " + gender);

        List<Person> matchingPeople = new ArrayList<>();
        for (Person person : people) {
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
