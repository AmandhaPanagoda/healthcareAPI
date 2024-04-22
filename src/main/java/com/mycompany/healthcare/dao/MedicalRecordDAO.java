/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.healthcare.model.MedicalRecord;
import com.mycompany.healthcare.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class MedicalRecordDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordDAO.class);
    private static final List<MedicalRecord> medicalRecords = new ArrayList<>();

    static {
        Patient patient1 = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Patient patient2 = new Patient(2, 4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");
        Patient patient3 = new Patient(3, 5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels");
        Patient patient4 = new Patient(4, 6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month");

        medicalRecords.add(new MedicalRecord(1, patient1, "Penicillin and related antibiotics", "ADHD", "Every two week checkup and psychological counseling", "O+"));
        medicalRecords.add(new MedicalRecord(2, patient2, "Ibuprofen", "None", "Annual checkup", "AB-"));
        medicalRecords.add(new MedicalRecord(3, patient3, "Insulin", "Diabetes", "Monthly checkups", "B+"));
        medicalRecords.add(new MedicalRecord(4, patient4, "Painkillers", "Appendectomy", "Follow-up appointment", "A-"));
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        LOGGER.info("Retrieving all medicalRecords");
        return medicalRecords;
    }

    public MedicalRecord getMedicalRecordById(int medicalRecordId) {
        LOGGER.info("Retrieving medical record by ID " + medicalRecordId);
        for (MedicalRecord medicalRecord : medicalRecords) {
            if (medicalRecord.getMedicalRecordId() == medicalRecordId) {
                return medicalRecord;
            }
        }
        LOGGER.info("Medical record by ID " + medicalRecordId + " was not found");
        return null;
    }

    public MedicalRecord getMedicalRecordByPatientId(int patientId) {
        LOGGER.info("Retrieving medical record by Patient ID " + patientId);
        for (MedicalRecord medicalRecord : medicalRecords) {
            int medicalRecordPatientID = (medicalRecord.getPatient()).getPatientId();
            if (patientId == medicalRecordPatientID) {
                return medicalRecord;
            }
        }
        LOGGER.info("Medical record of the patient ID " + patientId + " was not found");
        return null;
    }

    public int addMedicalRecord(MedicalRecord medicalRecord) {
        LOGGER.info("Adding a new medicalRecord");
        Helper<MedicalRecord> helper = new Helper<>();

        int newMedicalRecordId = helper.getNextId(medicalRecords, MedicalRecord::getMedicalRecordId); //get the next medical record id
        medicalRecord.setMedicalRecordId(newMedicalRecordId); // set the new medical record id
        medicalRecords.add(medicalRecord);
        LOGGER.info("New medical record with ID " + newMedicalRecordId + " was added to medical records list");

        return newMedicalRecordId;
    }

    public void updateMedicalRecord(MedicalRecord updatedMedicalRecord) {
        LOGGER.info("Update medical record");
        for (int i = 0; i < medicalRecords.size(); i++) {
            MedicalRecord medicalRecord = medicalRecords.get(i);
            if (medicalRecord.getMedicalRecordId() == updatedMedicalRecord.getMedicalRecordId()) {
                updatedMedicalRecord.setMedicalRecordId(medicalRecord.getMedicalRecordId());
                medicalRecords.set(i, updatedMedicalRecord);
                LOGGER.info("Medical record was updated. MedicalRecord ID : " + updatedMedicalRecord.getMedicalRecordId());
                return;
            }
        }
    }

    public boolean deleteMedicalRecord(int medicalRecordId) {
        boolean removed = medicalRecords.removeIf(medicalRecord -> {
            if (medicalRecord.getMedicalRecordId() == medicalRecordId) {
                LOGGER.info("Deleting the Medical Record with ID: " + medicalRecordId);
                return true;
            }
            return false;
        });
        return removed;
    }

    public List<MedicalRecord> searchMedicalRecords(String patientFirstName, String patientLastName, String bloodGroup) {
        LOGGER.info("Searching for medical records in the given criteria: patientFirstName: " + patientFirstName + ", patientLastName: " + patientLastName + ", bloodGroup: " + bloodGroup);

        List<MedicalRecord> matchingMedicalRecords = new ArrayList<>();

        for (MedicalRecord medicalRecord : medicalRecords) {

            Patient patient = medicalRecord.getPatient();

            boolean matchPatientFirstName = patientFirstName == null || patientFirstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchPatientLastName = patientLastName == null || patientLastName.equalsIgnoreCase(patient.getLastName());
            boolean matchBloodGroup = bloodGroup == null || bloodGroup.equalsIgnoreCase(medicalRecord.getBloodGroup());

            if (matchPatientFirstName && matchPatientLastName && matchBloodGroup) {
                matchingMedicalRecords.add(medicalRecord);
            }
        }
        return matchingMedicalRecords;
    }
}
