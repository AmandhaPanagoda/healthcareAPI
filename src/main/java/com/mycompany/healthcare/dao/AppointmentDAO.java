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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class AppointmentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentDAO.class);
    private static final List<Appointment> appointments = new ArrayList<>();

    static {
        Doctor doctor1 = new Doctor(1, "Cardiologist", 1, "Eric", "Anderson", 1234548548, "684 Delaware Avenue, SF", "M", 45);
        Patient patient1 = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");

        appointments.add(new Appointment(1, "12-13-2024", "16:00:00", patient1, doctor1));

        Doctor doctor2 = new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);
        Patient patient2 = new Patient(2, 4, "Alice", "Smith", 1234567890, "123 Main St, Anytown, USA", "F", 25, "Healthy", "No significant medical history");

        appointments.add(new Appointment(2, "12-14-2024", "10:00:00", patient2, doctor2));

        Doctor doctor3 = new Doctor(3, "Pediatrician", 3, "Michael", "Brown", 1876543210, "456 Elm St, Othertown, USA", "M", 40);
        Patient patient3 = new Patient(3, 5, "Bob", "Johnson", 1876543210, "456 Elm St, Othertown, USA", "M", 35, "Diagnosed with diabetes", "Regularly monitored for blood sugar levels");

        appointments.add(new Appointment(3, "12-15-2024", "14:30:00", patient3, doctor3));

        Doctor doctor4 = new Doctor(4, "Dermatologist", 4, "Emily", "Clark", 1876543210, "789 Oak St, Anotherplace, USA", "F", 35);
        Patient patient4 = new Patient(4, 6, "Charlie", "Brown", 1551234567, "789 Oak St, Anotherplace, USA", "M", 45, "Recovering from surgery", "Underwent appendectomy last month");

        appointments.add(new Appointment(4, "12-16-2024", "11:45:00", patient4, doctor4));

    }

    public List<Appointment> getAllAppointments() {
        LOGGER.info("Retrieving all appointments");
        return appointments;
    }

    public Appointment getAppointmentById(int appointmentId) {
        LOGGER.info("Retrieving appointment by ID " + appointmentId);
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId() == appointmentId) {
                return appointment;
            }
        }
        LOGGER.info("Apointment ID " + appointmentId + " does not exist");
        return null;
    }
    
     public List<Appointment> getAppointmentbyPatientId(int patientId) {
        LOGGER.info("Retrieving apointment by Patient ID " + patientId);
        List<Appointment> matchingAppointments = new ArrayList<>();
        
        for (Appointment appointment : appointments) {
            int appointmentPatientID = (appointment.getPatient()).getPatientId();
            if (patientId == appointmentPatientID) {
                matchingAppointments.add(appointment);
            }
        }

        return matchingAppointments;
    }

    public int addAppointment(Appointment appointment) {
        LOGGER.info("Adding a new appointment");
        Helper<Appointment> helper = new Helper<>();

        int newAppointmentId = helper.getNextId(appointments, Appointment::getAppointmentId);
        appointment.setAppointmentId(newAppointmentId);
        appointments.add(appointment);
        LOGGER.info("New appointment with ID " + newAppointmentId + " was added to appointments list");

        return newAppointmentId;
    }

    public void updateAppointment(Appointment updatedAppointment) {
        LOGGER.info("Update appointment");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (appointment.getAppointmentId() == updatedAppointment.getAppointmentId()) {
                updatedAppointment.setAppointmentId(appointment.getAppointmentId());
                appointments.set(i, updatedAppointment);
                LOGGER.info("Appointment was updated. Appointment ID : " + updatedAppointment.getAppointmentId());
                return;
            }
        }
    }

    public boolean deleteAppointment(int appointmentId) {
        LOGGER.info("Deleting appointment");
        boolean removed = appointments.removeIf(appointment -> {
            if (appointment.getAppointmentId() == appointmentId) {
                LOGGER.info("Appointment with ID: " + appointmentId + " was successfully deleted");
                return true;
            }
            return false;
        });
        return removed;
    }

    public List<Appointment> searchAppointments(String patientFirstName, String patientLastName, String doctorFirstName, String doctorLastName, String fromDateStr, String toDateStr, String specialization) {
        LOGGER.info("Searching for appointments in the given criteria. patientFirstName: " + patientFirstName
                + " patientLastName: " + patientLastName
                + " doctorFirstName: " + doctorFirstName
                + " fromDateStr: " + fromDateStr
                + " toDateStr: " + toDateStr
                + " specialization: " + specialization);

        List<Appointment> matchingAppointments = new ArrayList<>();

        // Parse fromDate and toDate strings to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
            LOGGER.error("Error parsing dates: " + e.getMessage());
            return matchingAppointments;
        }

        for (Appointment appointment : appointments) {

            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            // Parse appointment date string to Date object
            Date appointmentDate = null;
            try {
                appointmentDate = dateFormat.parse(appointment.getDate());
            } catch (ParseException e) {
                LOGGER.error("Error parsing appointment date: " + e.getMessage());
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
