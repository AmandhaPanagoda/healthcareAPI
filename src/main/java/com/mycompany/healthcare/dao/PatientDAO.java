/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.healthcare.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class PatientDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDAO.class);
    private static final List<Patient> patients = new ArrayList<>();

    static {
        patients.add(new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory"));
        patients.add(new Patient(2, 4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history"));
        patients.add(new Patient(3, 5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels"));
        patients.add(new Patient(4, 6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month"));
    }

    public List<Patient> getAllPatients() {
        LOGGER.info("Retrieving all patients");
        return patients;
    }

    public Patient getPatientById(int patientId) {
        LOGGER.info("Retrieving patient by ID " + patientId);
        for (Patient patient : patients) {
            if (patient.getPatientId() == patientId) {
                return patient;
            }
        }
        LOGGER.info("Patient by ID " + patientId + " was not found");
        return null;
    }

    public int addPatient(Patient patient) {
        LOGGER.info("Adding a new patient");
        
        Helper<Patient> helper = new Helper<>();
        int newPatientId = helper.getNextId(patients, Patient::getPatientId); //get new patient ID
        
        patient.setPatientId(newPatientId); // set the new patient ID
        patients.add(patient);
        LOGGER.info("New patient with ID " + newPatientId + " was added to patient list");
        
        return newPatientId;
    }

    public void updatePatient(Patient updatePatient) {
        LOGGER.info("Update patient record");
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.getPatientId() == updatePatient.getPatientId()) {
                updatePatient.setPersonId(patient.getPersonId());
                patients.set(i, updatePatient);
                LOGGER.info("Patient record was updated. Patient ID : " + updatePatient.getPatientId());
                return;
            }
        }
    }

    public boolean deletePatient(int patientId) {
        boolean removed = patients.removeIf(patient -> {
            if (patient.getPatientId() == patientId) {
                LOGGER.info("Deleting the patient with ID: " + patientId);
                return true;
            }
            return false;
        });
        return removed;
    }

}
