/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Represents an appointment between the doctor and the patient
 * 
 * @author Amandha
 */
public class Appointment {

    private Integer appointmentId;

    @NotEmpty(message = "Date is required")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Date must be in the format dd-mm-yyyy")
    private String date;

    @NotEmpty(message = "Time is required")
    @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Time must be in the format hh:mm:ss")
    private String time;

    @NotNull(message = "Patient ID is required")
    private Patient patient;

    @NotNull(message = "Doctor ID is required")
    private Doctor doctor;

    public Appointment() {
    }

    public Appointment(int appointmentId, String date, String time, Patient patientName, Doctor doctorName) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.time = time;
        this.patient = patientName;
        this.doctor = doctorName;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

}
