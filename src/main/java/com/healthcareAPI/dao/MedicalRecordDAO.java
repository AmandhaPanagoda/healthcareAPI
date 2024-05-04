/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.dao;

import com.healthcareAPI.helper.Helper;
import com.healthcareAPI.model.MedicalRecord;
import com.healthcareAPI.model.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) class for managing MedicalRecord objects. Provides
 * methods for CRUD operations and searching for medical records.
 *
 * @author Amandha
 */
public class MedicalRecordDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordDAO.class);
    private static final Map<Integer, MedicalRecord> medicalRecords = new HashMap<>();

    // Initialize some sample data
    static {
        Patient patient1 = new Patient(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Patient patient2 = new Patient(4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");
        Patient patient3 = new Patient(5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 15, "Mild allergies", "No major illnesses");
        Patient patient4 = new Patient(6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "High blood pressure", "Previous surgery for appendicitis");

        MedicalRecord medicalRecord1 = new MedicalRecord(1, patient1, "Penicillin and related antibiotics", "ADHD", "Every two week checkup and psychological counseling", "O+");
        MedicalRecord medicalRecord2 = new MedicalRecord(2, patient2, "Ibuprofen", "None", "Annual checkup", "AB-");
        MedicalRecord medicalRecord3 = new MedicalRecord(3, patient3, "Insulin", "Diabetes", "Monthly checkups", "B+");
        MedicalRecord medicalRecord4 = new MedicalRecord(4, patient4, "Painkillers", "Appendectomy", "Follow-up appointment", "A-");

        medicalRecords.put(1, medicalRecord1);
        medicalRecords.put(2, medicalRecord2);
        medicalRecords.put(3, medicalRecord3);
        medicalRecords.put(4, medicalRecord4);
    }

    /**
     * Retrieve all medical records.
     *
     * @return A map of medicalRecordId to MedicalRecord objects.
     */
    public Map<Integer, MedicalRecord> getAllMedicalRecords() {
        LOGGER.info("Retrieving all medicalRecords");
        return medicalRecords;
    }

    /**
     * Retrieve a medical record by its ID.
     *
     * @param medicalRecordId The ID of the medical record to retrieve.
     * @return The MedicalRecord object, or null if not found.
     */
    public MedicalRecord getMedicalRecordById(int medicalRecordId) {
        LOGGER.info("Retrieving medical record by ID " + medicalRecordId);
        return medicalRecords.get(medicalRecordId);
    }

    /**
     * Retrieve a medical record by the ID of its patient.
     *
     * @param patientId The ID of the patient.
     * @return The MedicalRecord object, or null if not found.
     */
    public MedicalRecord getMedicalRecordByPatientId(int patientId) {
        LOGGER.info("Retrieving medical record by Patient ID " + patientId);
        return medicalRecords.values().stream()
                .filter(medicalRecord -> medicalRecord.getPatient().getPersonId() == patientId)
                .findFirst()
                .orElse(null); // Return null if no record is found
    }

    /**
     * Add a new medical record.
     *
     * @param medicalRecord The MedicalRecord object to add.
     * @return The ID of the newly added medical record.
     */
    public int addMedicalRecord(MedicalRecord medicalRecord) {
        try {
            Helper<MedicalRecord> helper = new Helper<>();
            int newMedicalRecordId = helper.getNextId(medicalRecords); // generate the next medical record id
            medicalRecord.setMedicalRecordId(newMedicalRecordId); // set the new medical record id

            medicalRecords.put(newMedicalRecordId, medicalRecord);

            LOGGER.info("New medical record with ID " + newMedicalRecordId + " was added to medical records list");
            return newMedicalRecordId;
        } catch (Exception e) {
            LOGGER.error("Error adding medical record: " + e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Update an existing medical record.
     *
     * @param updatedMedicalRecord The updated MedicalRecord object.
     */
    public void updateMedicalRecord(MedicalRecord updatedMedicalRecord) {
        try {
            medicalRecords.put(updatedMedicalRecord.getMedicalRecordId(), updatedMedicalRecord);
            LOGGER.info("Medical record was updated. MedicalRecord ID : " + updatedMedicalRecord.getMedicalRecordId());
        } catch (Exception e) {
            LOGGER.error("Medical record ID: " + updatedMedicalRecord.getMedicalRecordId() + ". Error updating medical record: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a medical record.
     *
     * @param medicalRecordId The ID of the medical record to delete.
     * @return true if the medical record was deleted, false otherwise.
     */
    public boolean deleteMedicalRecord(int medicalRecordId) {
        try {
            MedicalRecord removedRecord = medicalRecords.remove(medicalRecordId);
            if (removedRecord != null) {
                LOGGER.info("Medical Record with ID {} was successfully deleted", medicalRecordId);
                return true;
            } else {
                LOGGER.info("Medical Record with ID {} was not found", medicalRecordId);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting Medical Record with ID {}: {}", medicalRecordId, e.getMessage());
            return false;
        }
    }

    /**
     * Search for medical records based on patient criteria.
     *
     * @param patientFirstName The first name of the patient.
     * @param patientLastName The last name of the patient.
     * @param bloodGroup The blood group of the patient.
     * @return A list of matching medicalRecordId to MedicalRecord objects.
     */
    public List<MedicalRecord> searchMedicalRecords(String patientFirstName, String patientLastName, String bloodGroup) {
        LOGGER.info("Searching for medical records in the given criteria: patientFirstName: " + patientFirstName + ", patientLastName: " + patientLastName + ", bloodGroup: " + bloodGroup);

        List<MedicalRecord> matchingMedicalRecords = new ArrayList<>();

        for (MedicalRecord medicalRecord : medicalRecords.values()) {
            Patient patient = medicalRecord.getPatient();

            boolean matchPatientFirstName = patientFirstName == null || patientFirstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchPatientLastName = patientLastName == null || patientLastName.equalsIgnoreCase(patient.getLastName());
            boolean matchBloodGroup = bloodGroup == null || bloodGroup.equalsIgnoreCase(medicalRecord.getBloodGroup());

            if (matchPatientFirstName && matchPatientLastName && matchBloodGroup) {
                matchingMedicalRecords.add(medicalRecord); // add the matching medical record to the list
            }
        }
        return matchingMedicalRecords;
    }
}
