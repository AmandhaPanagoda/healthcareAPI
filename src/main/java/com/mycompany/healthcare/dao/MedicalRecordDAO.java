/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import com.mycompany.healthcare.model.MedicalRecord;
import com.mycompany.healthcare.model.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) class for managing MedicalRecord objects.
 * Provides methods for CRUD operations and searching for medical records.
 * 
 * @author Amandha
 */
public class MedicalRecordDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordDAO.class);
    private static final Map<Integer, MedicalRecord> medicalRecords = new HashMap<>();

    static {
        // Initialize some sample data
        Patient patient1 = new Patient(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Patient patient2 = new Patient(4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");
        Patient patient3 = new Patient(5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels");
        Patient patient4 = new Patient(6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month");

        medicalRecords.put(1, new MedicalRecord(1, patient1, "Penicillin and related antibiotics", "ADHD", "Every two week checkup and psychological counseling", "O+"));
        medicalRecords.put(2, new MedicalRecord(2, patient2, "Ibuprofen", "None", "Annual checkup", "AB-"));
        medicalRecords.put(3, new MedicalRecord(3, patient3, "Insulin", "Diabetes", "Monthly checkups", "B+"));
        medicalRecords.put(4, new MedicalRecord(4, patient4, "Painkillers", "Appendectomy", "Follow-up appointment", "A-"));
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
        for (MedicalRecord medicalRecord : medicalRecords.values()) {
            if (medicalRecord.getPatient().getPersonId() == patientId) {
                return medicalRecord;
            }
        }
        LOGGER.info("Medical record of the patient ID " + patientId + " was not found");
        return null;
    }

    /**
     * Add a new medical record.
     *
     * @param medicalRecord The MedicalRecord object to add.
     * @return The ID of the newly added medical record.
     */
    public int addMedicalRecord(MedicalRecord medicalRecord) {
        LOGGER.info("Adding a new medicalRecord");
        Helper<MedicalRecord> helper = new Helper<>();

        int newMedicalRecordId = helper.getNextId(medicalRecords); //get the next medical record id
        medicalRecord.setMedicalRecordId(newMedicalRecordId); // set the new medical record id
        medicalRecords.put(newMedicalRecordId, medicalRecord);
        LOGGER.info("New medical record with ID " + newMedicalRecordId + " was added to medical records list");

        return newMedicalRecordId;
    }

    /**
     * Update an existing medical record.
     *
     * @param updatedMedicalRecord The updated MedicalRecord object.
     */
    public void updateMedicalRecord(MedicalRecord updatedMedicalRecord) {
        LOGGER.info("Update medical record");
        medicalRecords.put(updatedMedicalRecord.getMedicalRecordId(), updatedMedicalRecord);
        LOGGER.info("Medical record was updated. MedicalRecord ID : " + updatedMedicalRecord.getMedicalRecordId());
    }

    /**
     * Delete a medical record.
     *
     * @param medicalRecordId The ID of the medical record to delete.
     * @return true if the medical record was deleted, false otherwise.
     */
    public boolean deleteMedicalRecord(int medicalRecordId) {
        LOGGER.info("Deleting the Medical Record with ID: " + medicalRecordId);
        return medicalRecords.remove(medicalRecordId) != null;
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
                matchingMedicalRecords.add(medicalRecord.getMedicalRecordId(), medicalRecord);
            }
        }
        return matchingMedicalRecords;
    }
}
