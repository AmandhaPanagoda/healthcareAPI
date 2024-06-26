/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.dao;

import com.healthcareAPI.helper.ObjectPatcherHelper;
import com.healthcareAPI.model.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) class for managing Patient objects. Provides methods
 * for retrieving, adding, updating, and deleting patient records. Includes
 * static data for initial patients.
 *
 * @author Amandha
 */
public class PatientDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDAO.class);
    private static final Map<Integer, Patient> patients = new HashMap<>();
    
    // Initialize some sample data
    static {
        Patient patient1 = new Patient(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Patient patient2 = new Patient(4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");
        Patient patient3 = new Patient(5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 15, "Mild allergies", "No major illnesses");
        Patient patient4 = new Patient(6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "High blood pressure", "Previous surgery for appendicitis");
        Patient patient5 = new Patient(8, "Grace", "Lee", 1765432987, "890 Maple Ave, Nowhere, USA", "F", 40, "Type 2 diabetes", "Family history of heart disease");
        
        patients.put(3, patient1);
        patients.put(4, patient2);
        patients.put(5, patient3);
        patients.put(6, patient4);
        patients.put(8, patient5);
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
     *
     */
    public void addPatient(Patient patient) {
        try {
            patients.put(patient.getPersonId(), patient);
            LOGGER.info("New patient with ID {} was added to patients list", patient.getPersonId());
        } catch (Exception e) {
            LOGGER.error("Error adding patient: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a patient's information.
     *
     * @param updatedPatient The updated patient information.
     */
    public void updatePatient(Patient updatedPatient) {
        try {
            patients.put(updatedPatient.getPersonId(), updatedPatient);
            LOGGER.info("Patient record was updated. Patient ID : " + updatedPatient.getPersonId());
        } catch (Exception e) {
            LOGGER.error("Patient ID: " + updatedPatient.getPersonId() + ". Error updating patient: " + e.getMessage(), e);
        }
    }

    /**
     * Partially updates an existing patient record by applying non-null fields
     * from the partial updated patient object.
     *
     * @param existingPatient The existing patient object to update.
     * @param partialUpdatedPatient The partial updated patient object
     * containing the new values.
     */
    public void partialUpdatePatient(Patient existingPatient, Patient partialUpdatedPatient) {
        try {
            LOGGER.info("Updating the patient record");
            ObjectPatcherHelper.objectPatcher(existingPatient, partialUpdatedPatient);
        } catch (IllegalAccessException e) {
            LOGGER.error("An error occured: " + e.getMessage());
        }
    }

    /**
     * Deletes a patient by ID.
     *
     * @param patientId The ID of the patient to delete.
     * @return True if the patient was successfully deleted, false otherwise.
     */
    public boolean deletePatient(int patientId) {
        try {
            Patient removedPatient = patients.remove(patientId);
            if (removedPatient != null) {
                LOGGER.info("Patient with ID {} was successfully deleted", patientId);
                return true;
            } else {
                LOGGER.info("Patient with ID {} was not found", patientId);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting Patient with ID {}: {}", patientId, e.getMessage());
            return false;
        }
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
                matchingPatients.add(patient); // add the matching patient record to the list
            }
        }
        return matchingPatients;
    }

}
