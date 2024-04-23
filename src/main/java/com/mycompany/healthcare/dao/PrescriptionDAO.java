/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import com.mycompany.healthcare.model.Prescription;
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
public class PrescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionDAO.class);
    private static final Map<Integer, Prescription> prescriptions = new HashMap<>();

    static {
        Patient patient = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Doctor doctor = new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);

        prescriptions.put(1, new Prescription(1, patient, doctor, "01-04-2024", "Medication 1", "Take with food", "1 pill", "1 week"));
        prescriptions.put(2, new Prescription(2, patient, doctor, "05-04-2024", "Medication 2", "Take before bedtime", "2 pills", "2 weeks"));
        prescriptions.put(3, new Prescription(3, patient, doctor, "10-04-2024", "Medication 3", "Take with plenty of water", "1 pill", "3 weeks"));
    }

    /**
     * Retrieves all prescriptions from the database.
     *
     * @return A list of all prescriptions.
     */
    public Map<Integer, Prescription> getAllPrescriptions() {
        LOGGER.info("Retrieving all prescriptions");
        return prescriptions;
    }

    /**
     * Retrieves a prescription by its unique ID.
     *
     * @param prescriptionId The ID of the prescription to retrieve.
     * @return The prescription object if found, otherwise null.
     */
    public Prescription getPrescriptionById(int prescriptionId) {
        LOGGER.info("Retrieving prescription by ID " + prescriptionId);
        return prescriptions.getOrDefault(prescriptionId, null);
    }

    /**
     * Retrieves prescriptions associated with a patient ID.
     *
     * @param patientId The ID of the patient.
     * @return A list of prescriptions associated with the patient.
     */
    public List<Prescription> getPrescriptionByPatientId(int patientId) {
        LOGGER.info("Retrieving prescription by Patient ID " + patientId);

        List<Prescription> matchingPrescriptions = new ArrayList<>();

        for (Prescription prescription : prescriptions.values()) {
            int prescriptionPatientID = prescription.getPrescribedFor().getPatientId();
            if (patientId == prescriptionPatientID) {
                matchingPrescriptions.add(prescription);
            }
        }
        return matchingPrescriptions;
    }
    
     /**
     * Retrieves prescriptions associated with a doctor ID.
     *
     * @param doctorId The ID of the patient.
     * @return A list of prescriptions associated with the doctor.
     */
    public List<Prescription> getPrescriptionByDoctorId(int doctorId) {
        LOGGER.info("Retrieving prescription by Doctor ID " + doctorId);

        List<Prescription> matchingPrescriptions = new ArrayList<>();

        for (Prescription prescription : prescriptions.values()) {
            int prescriptionDoctorID = prescription.getPrescribedBy().getDoctorId();
            if (doctorId == prescriptionDoctorID) {
                matchingPrescriptions.add(prescription);
            }
        }
        return matchingPrescriptions;
    }

    /**
     * Adds a new prescription to the database.
     *
     * @param prescription The prescription object to add.
     * @return The ID assigned to the new prescription.
     */
    public int addPrescription(Prescription prescription) {
        LOGGER.info("Adding a new prescription");

        Helper<Prescription> helper = new Helper<>();
        int newPrescriptionId = helper.getNextId(prescriptions);

        prescription.setPrescriptionId(newPrescriptionId);
        prescriptions.put(newPrescriptionId, prescription);
        LOGGER.info("New prescription with ID " + newPrescriptionId + " is added to prescriptions list");

        return newPrescriptionId;
    }

    /**
     * Updates an existing prescription record in the database.
     *
     * @param updatedPrescription The updated prescription object.
     */
    public void updatePrescription(Prescription updatedPrescription) {
        LOGGER.info("Update prescription");
        Prescription prescription = prescriptions.get(updatedPrescription.getPrescriptionId());
        if (prescription != null) {
            prescriptions.put(updatedPrescription.getPrescriptionId(), updatedPrescription);
            LOGGER.info("Prescription was updated. Prescription ID : " + updatedPrescription.getPrescriptionId());
        } else {
            LOGGER.warn("Prescription with ID " + updatedPrescription.getPrescriptionId() + " not found, cannot update");
        }
    }

    /**
     * Deletes a prescription record from the database.
     *
     * @param prescriptionId The ID of the prescription to delete.
     * @return True if the prescription was successfully deleted, otherwise
     * false.
     */
    public boolean deletePrescription(int prescriptionId) {
        Prescription removed = prescriptions.remove(prescriptionId);
        if (removed != null) {
            LOGGER.info("Deleting the Prescription with ID: " + prescriptionId);
            return true;
        } else {
            LOGGER.warn("Prescription with ID " + prescriptionId + " not found, cannot delete");
            return false;
        }
    }
}
