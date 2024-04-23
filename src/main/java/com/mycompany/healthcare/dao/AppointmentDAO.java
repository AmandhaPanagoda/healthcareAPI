/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mycompany.healthcare.model.Appointment;
import com.mycompany.healthcare.model.Doctor;
import com.mycompany.healthcare.model.Patient;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class AppointmentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentDAO.class);
    private static final Map<Integer, Appointment> appointments = new HashMap<>();

    // Static block to initialize some sample appointment records
    static {
        Doctor doctor1 = new Doctor(1, "Cardiologist", 1, "Eric", "Anderson", 1234548548, "684 Delaware Avenue, SF", "M", 45);
        Patient patient1 = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");

        appointments.put(1, new Appointment(1, "12-13-2024", "16:00:00", patient1, doctor1));

        Doctor doctor2 = new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);
        Patient patient2 = new Patient(2, 4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");

        appointments.put(2, new Appointment(2, "12-14-2024", "10:00:00", patient2, doctor2));

        Doctor doctor3 = new Doctor(3, "Pediatrician", 3, "Michael", "Brown", 1876543210, "456 Elm St, Othertown, USA", "M", 40);
        Patient patient3 = new Patient(3, 5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels");

        appointments.put(3, new Appointment(3, "12-15-2024", "14:30:00", patient3, doctor3));

        Doctor doctor4 = new Doctor(4, "Dermatologist", 4, "Emily", "Clark", 1876543210, "789 Oak St, Anotherplace, USA", "F", 35);
        Patient patient4 = new Patient(4, 6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month");

        appointments.put(4, new Appointment(4, "12-16-2024", "11:45:00", patient4, doctor4));
    }

    /**
     * Retrieves all appointments from the database.
     *
     * @return A map of all appointments indexed by their IDs.
     */
    public Map<Integer, Appointment> getAllAppointments() {
        LOGGER.info("Retrieving all appointments");
        return appointments;
    }

    /**
     * Retrieves an appointment by its unique ID.
     *
     * @param appointmentId The ID of the appointment to retrieve.
     * @return The appointment object if found, otherwise null.
     */
    public Appointment getAppointmentById(int appointmentId) {
        LOGGER.info("Retrieving appointment by ID {}", appointmentId);
        return appointments.get(appointmentId);
    }

    /**
     * Retrieves appointments associated with a patient ID.
     *
     * @param patientId The ID of the patient.
     * @return A list of appointments associated with the patient.
     */
    public List<Appointment> getAppointmentByPatientId(int patientId) {
        LOGGER.info("Retrieving appointments by Patient ID {}", patientId);
        List<Appointment> matchingAppointments = new ArrayList<>();

        for (Appointment appointment : appointments.values()) {
            int appointmentPatientID = appointment.getPatient().getPatientId();
            if (patientId == appointmentPatientID) {
                matchingAppointments.add(appointment);
            }
        }

        return matchingAppointments;
    }

    /**
     * Retrieves appointments associated with a doctor ID.
     *
     * @param doctorId The ID of the patient.
     * @return A list of appointments associated with the doctor.
     */
    public List<Appointment> getAppointmentByDoctorId(int doctorId) {
        LOGGER.info("Retrieving appointments by Doctor ID {}", doctorId);
        List<Appointment> matchingAppointments = new ArrayList<>();

        for (Appointment appointment : appointments.values()) {
            int appointmentDoctorID = appointment.getDoctor().getDoctorId();
            if (doctorId == appointmentDoctorID) {
                matchingAppointments.add(appointment);
            }
        }

        return matchingAppointments;
    }

    /**
     * Adds a new appointment to the database.
     *
     * @param appointment The appointment object to add.
     * @return The ID assigned to the new appointment.
     */
    public int addAppointment(Appointment appointment) {
        LOGGER.info("Adding a new appointment");
        Helper<Appointment> helper = new Helper<>();

        int newAppointmentId = helper.getNextId(appointments);
        appointment.setAppointmentId(newAppointmentId);
        appointments.put(newAppointmentId, appointment);
        LOGGER.info("New appointment with ID {} was added to appointments list", newAppointmentId);

        return newAppointmentId;
    }

    /**
     * Updates an existing appointment record in the database.
     *
     * @param updatedAppointment The updated appointment object.
     */
    public void updateAppointment(Appointment updatedAppointment) {
        LOGGER.info("Updating appointment");
        Appointment existingAppointment = appointments.get(updatedAppointment.getAppointmentId());
        if (existingAppointment != null) {
            appointments.put(updatedAppointment.getAppointmentId(), updatedAppointment);
            LOGGER.info("Appointment was updated. Appointment ID : {}", updatedAppointment.getAppointmentId());
        } else {
            LOGGER.info("Appointment with ID {} was not found", updatedAppointment.getAppointmentId());
        }
    }

    /**
     * Deletes an appointment record from the database.
     *
     * @param appointmentId The ID of the appointment to delete.
     * @return True if the appointment was successfully deleted, otherwise
     * false.
     */
    public boolean deleteAppointment(int appointmentId) {
        LOGGER.info("Deleting appointment with ID: {}", appointmentId);
        Appointment removedAppointment = appointments.remove(appointmentId);
        if (removedAppointment != null) {
            LOGGER.info("Appointment with ID {} was successfully deleted", appointmentId);
            return true;
        } else {
            LOGGER.info("Appointment with ID {} was not found", appointmentId);
            return false;
        }
    }

    /**
     * Searches for appointments in the database based on specified criteria.
     *
     * @param patientFirstName The first name of the patient.
     * @param patientLastName The last name of the patient.
     * @param doctorFirstName The first name of the doctor.
     * @param doctorLastName The last name of the doctor.
     * @param fromDateStr The start date for the appointment search range.
     * @param toDateStr The end date for the appointment search range.
     * @param specialization The specialization of the doctor.
     * @return A list of matching appointments.
     */
    public List<Appointment> searchAppointments(String patientFirstName, String patientLastName, String doctorFirstName, String doctorLastName, String fromDateStr, String toDateStr, String specialization) {
        LOGGER.info("Searching for appointments with criteria - Patient First Name: {}, Patient Last Name: {}, Doctor First Name: {}, Doctor Last Name: {}, From Date: {}, To Date: {}, Specialization: {}",
                patientFirstName, patientLastName, doctorFirstName, doctorLastName, fromDateStr, toDateStr, specialization);

        List<Appointment> matchingAppointments = new ArrayList<>();

        // Parse fromDate and toDate strings to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date fromDate = null;
        Date toDate = null;

        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = dateFormat.parse(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = dateFormat.parse(toDateStr);
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing dates: {}", e.getMessage());
            return matchingAppointments;
        }

        for (Appointment appointment : appointments.values()) {

            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            // Parse appointment date string to Date object
            Date appointmentDate = null;
            try {
                appointmentDate = dateFormat.parse(appointment.getDate());
            } catch (ParseException e) {
                LOGGER.error("Error parsing appointment date: {}", e.getMessage());
            }

            boolean matchPatientFirstName = patientFirstName == null || patientFirstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchPatientLastName = patientLastName == null || patientLastName.equalsIgnoreCase(patient.getLastName());
            boolean matchDoctorFirstName = doctorFirstName == null || doctorFirstName.equalsIgnoreCase(doctor.getFirstName());
            boolean matchDoctorLastName = doctorLastName == null || doctorLastName.equalsIgnoreCase(doctor.getLastName());
            boolean matchSpecialization = specialization == null || specialization.equalsIgnoreCase(doctor.getSpecialization());
            boolean matchDateRange = (fromDate == null || appointmentDate.compareTo(fromDate) >= 0)
                    && (toDate == null || appointmentDate.compareTo(toDate) <= 0);

            if (matchPatientFirstName && matchPatientLastName && matchDoctorFirstName
                    && matchDoctorLastName && matchSpecialization && matchDateRange) {
                matchingAppointments.add(appointment);
            }
        }
        return matchingAppointments;
    }
}
