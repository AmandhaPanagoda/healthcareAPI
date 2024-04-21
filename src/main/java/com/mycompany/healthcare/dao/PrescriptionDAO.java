/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import com.mycompany.healthcare.model.Prescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class PrescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionDAO.class);
    private static final List<Prescription> prescriptions = new ArrayList<>();

    static {
        Patient patient = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Doctor doctor = new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);

        prescriptions.add(new Prescription(1, patient, doctor, "01-04-2024", "Medication 1", "Take with food", "1 pill", "1 week"));
        prescriptions.add(new Prescription(2, patient, doctor, "05-04-2024", "Medication 2", "Take before bedtime", "2 pills", "2 weeks"));
        prescriptions.add(new Prescription(3, patient, doctor, "10-04-2024", "Medication 3", "Take with plenty of water", "1 pill", "3 weeks"));

    }

    public List<Prescription> getAllPrescriptions() {
        LOGGER.info("Retrieving all prescriptions");
        return prescriptions;
    }

    public Prescription getPrescriptionById(int prescriptionId) {
        LOGGER.info("Retrieving prescription by ID " + prescriptionId);
        for (Prescription prescription : prescriptions) {
            if (prescription.getPrescriptionId() == prescriptionId) {
                return prescription;
            }
        }
        LOGGER.info("Prescription by ID " + prescriptionId + " was not found");
        return null;
    }

    public List<Prescription> getPrescriptionByPatientId(int patientId) {
        LOGGER.info("Retrieving prescription by Patient ID " + patientId);
        
        List<Prescription> matchingPrescriptions = new ArrayList<>();
        
        for (Prescription prescription : prescriptions) {
            int prescriptionPatientID = (prescription.getPrescribedFor().getPatientId());
            if (patientId == prescriptionPatientID) {
                matchingPrescriptions.add(prescription);
            }
        }
        return matchingPrescriptions;
    }

    public int addPrescription(Prescription prescription) {
        LOGGER.info("Adding a new prescritions");
        
        Helper<Prescription> helper = new Helper<>();
        int newPrescriptionId = helper.getNextId(prescriptions, Prescription::getPrescriptionId);
        
        prescription.setPrescriptionId(newPrescriptionId);
        prescriptions.add(prescription);
        LOGGER.info("New prescription with ID " + newPrescriptionId + " is added to prescriptions list");

        return newPrescriptionId;
    }

    public void updatePrescription(Prescription updatedPrescription) {
        LOGGER.info("Update prescription");
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription prescription = prescriptions.get(i);
            if (prescription.getPrescriptionId() == updatedPrescription.getPrescriptionId()) {
                updatedPrescription.setPrescriptionId(prescription.getPrescriptionId());
                prescriptions.set(i, updatedPrescription);
                LOGGER.info("Prescription was updated. Prescription ID : " + updatedPrescription.getPrescriptionId());
                return;
            }
        }
    }

    public boolean deletePrescription(int prescriptionId) {
        boolean removed = prescriptions.removeIf(prescription -> {
            if (prescription.getPrescriptionId() == prescriptionId) {
                LOGGER.info("Deleting the Prescription with ID: " + prescriptionId);
                return true;
            }
            return false;
        });
        return removed;
    }
}
