/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import static com.mycompany.healthcare.helper.SimpleDateFormatHelper.parseSimpleDate;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import com.mycompany.healthcare.model.Prescription;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) for managing prescriptions. This class provides
 * methods to interact with the database to perform CRUD (Create, Read, Update,
 * Delete) operations on Prescription objects.
 *
 * @author Amandha
 */
public class PrescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionDAO.class);
    private static final Map<Integer, Prescription> prescriptions = new HashMap<>();

    static {
        Patient patient = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        Doctor doctor = new Doctor("Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);

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
            int prescriptionDoctorID = prescription.getPrescribedBy().getPersonId();
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

    /**
     * Searches for prescriptions based on the specified criteria.
     *
     * @param patientFirstName The first name of the patient (optional).
     * @param patientLastName The last name of the patient (optional).
     * @param doctorFirstName The first name of the doctor (optional).
     * @param doctorLastName The last name of the doctor (optional).
     * @param fromDateStr The start date range for the prescription (optional,
     * format: "dd-MM-yyyy").
     * @param toDateStr The end date range for the prescription (optional,
     * format: "dd-MM-yyyy").
     * @return A list of prescriptions that match the specified criteria.
     */
    public List<Prescription> searchPrescriptions(String patientFirstName, String patientLastName, String doctorFirstName, String doctorLastName, String fromDateStr, String toDateStr) {
        LOGGER.info("Searching for prescriptions with criteria - Patient First Name: {}, Patient Last Name: {}, Doctor First Name: {}, Doctor Last Name: {}, From Date: {}, To Date: {}",
                patientFirstName, patientLastName, doctorFirstName, doctorLastName, fromDateStr, toDateStr);

        List<Prescription> matchingPrescriptions = new ArrayList<>();

        // Parse fromDate and toDate strings to Date objects
        Date fromDate = null;
        Date toDate = null;

        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = parseSimpleDate(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = parseSimpleDate(toDateStr);
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing dates: {}", e.getMessage());
            return matchingPrescriptions;
        }

        for (Prescription prescription : prescriptions.values()) {

            Patient patient = prescription.getPrescribedFor();
            Doctor doctor = prescription.getPrescribedBy();

            // Parse prescription date string to Date object
            Date prescriptionDate = null;
            try {
                prescriptionDate = parseSimpleDate(prescription.getPrescribedDate());
            } catch (ParseException e) {
                LOGGER.error("Error parsing the prescribed date: {}", e.getMessage());
            }

            boolean matchPatientFirstName = patientFirstName == null || patientFirstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchPatientLastName = patientLastName == null || patientLastName.equalsIgnoreCase(patient.getLastName());
            boolean matchDoctorFirstName = doctorFirstName == null || doctorFirstName.equalsIgnoreCase(doctor.getFirstName());
            boolean matchDoctorLastName = doctorLastName == null || doctorLastName.equalsIgnoreCase(doctor.getLastName());
            boolean matchDateRange = (fromDate == null || prescriptionDate.compareTo(fromDate) >= 0)
                    && (toDate == null || prescriptionDate.compareTo(toDate) <= 0);

            if (matchPatientFirstName && matchPatientLastName && matchDoctorFirstName
                    && matchDoctorLastName && matchDateRange) {
                matchingPrescriptions.add(prescription);
            }
        }
        return matchingPrescriptions;
    }
}
