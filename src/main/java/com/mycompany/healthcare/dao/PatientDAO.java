/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import com.mycompany.healthcare.model.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class PatientDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDAO.class);
    private static final Map<Integer, Patient> patients = new HashMap<>();

    static {
        patients.put(1, new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory"));
        patients.put(2, new Patient(2, 4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history"));
        patients.put(3, new Patient(3, 5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels"));
        patients.put(4, new Patient(4, 6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month"));
    }

    /**
     * Retrieves all patients.
     *
     * @return List of patients.
     */
    public Map<Integer, Patient> getAllPatients() {
        LOGGER.info("Retrieving all patients");
        return patients;
    }

    /**
     * Retrieves a patient by ID.
     *
     * @param patientId The ID of the patient to retrieve.
     * @return The patient with the specified ID, or null if not found.
     */
    public Patient getPatientById(int patientId) {
        LOGGER.info("Retrieving patient by ID " + patientId);
        return patients.get(patientId);
    }

    /**
     * Adds a new patient.
     *
     * @param patient The patient to add.
     * @return The ID of the newly added patient.
     */
    public int addPatient(Patient patient) {
        LOGGER.info("Adding a new patient");

        Helper<Patient> helper = new Helper<>();
        int newPatientId = helper.getNextId(patients); // Get new patient ID

        patient.setPatientId(newPatientId); // Set the new patient ID
        patients.put(newPatientId, patient);
        LOGGER.info("New patient with ID " + newPatientId + " was added to patient list");

        return newPatientId;
    }

    /**
     * Updates a patient's information.
     *
     * @param updatedPatient The updated patient information.
     */
    public void updatePatient(Patient updatedPatient) {
        LOGGER.info("Update patient record");

        Patient existingPatient = patients.get(updatedPatient.getPatientId());
        if (existingPatient != null) {
            updatedPatient.setPersonId(existingPatient.getPersonId());
            patients.put(updatedPatient.getPatientId(), updatedPatient);
            LOGGER.info("Patient record was updated. Patient ID : " + updatedPatient.getPatientId());
        }
    }

    /**
     * Deletes a patient by ID.
     *
     * @param patientId The ID of the patient to delete.
     * @return True if the patient was successfully deleted, false otherwise.
     */
    public boolean deletePatient(int patientId) {
        LOGGER.info("Deleting the patient with ID: " + patientId);
        return patients.remove(patientId) != null;
    }
    
    /**
     * Searches for patients based on the specified criteria.
     *
     * @param firstName The first name of the patient (optional).
     * @param lastName The last name of the patient (optional).
     * @param minAge The minimum age of the patient (inclusive, optional).
     * @param maxAge The maximum age of the patient (inclusive, optional).
     * @param gender The gender of the patient (optional).
     * @return A list of patients that match the specified criteria.
     */
    public List<Patient> searchPatients(String firstName, String lastName, Integer minAge, Integer maxAge, String gender) {
        LOGGER.info("Searching for patients with first name: {}, last name: {}, age range: {} - {}, and gender: {}",
                firstName, lastName, minAge, maxAge, gender);

        List<Patient> matchingPatients = new ArrayList<>();
        for (Patient patient : patients.values()) {
            boolean matchFirstName = firstName == null || firstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchLastName = lastName == null || lastName.equalsIgnoreCase(patient.getLastName());
            boolean matchAge = (minAge == null || patient.getAge() >= minAge) && (maxAge == null || patient.getAge() <= maxAge);
            boolean matchGender = gender == null || gender.equalsIgnoreCase(patient.getGender());

            if (matchFirstName && matchLastName && matchAge && matchGender) {
                matchingPatients.add(patient.getPatientId(), patient);
            }
        }
        return matchingPatients;
    }

}
