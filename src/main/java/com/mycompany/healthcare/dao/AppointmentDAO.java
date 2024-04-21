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

    public int addAppointment(Appointment appointment) {
        LOGGER.info("Adding a new appointment");
        Helper<Appointment> helper = new Helper<>();

        int newAppointmentId = helper.getNextId(appointments, Appointment::getAppointmentId);
        appointment.setAppointmentId(newAppointmentId);
        appointments.add(appointment);
        LOGGER.info("New appointment with ID " + newAppointmentId + " was added to appointments list");

        return newAppointmentId;
    }

    public void updateAppointment(Appointment updateAppointment) {
        LOGGER.info("Update appointment");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (appointment.getAppointmentId() == updateAppointment.getAppointmentId()) {
                updateAppointment.setAppointmentId(appointment.getAppointmentId());
                appointments.set(i, updateAppointment);
                LOGGER.info("Appointment was updated. Appointment ID : " + updateAppointment.getAppointmentId());
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
