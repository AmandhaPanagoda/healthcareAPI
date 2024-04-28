/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

/**
 * Model class representing a patient, extending the Person class. Includes
 * patient-specific fields such as patient ID, health status, and medical
 * history. Provides getter and setters for accessing and updating patient
 * information.
 *
 * @author Amandha
 */
public class Patient extends Person {

    private Integer patientId;
    private String healthStatus;
    private String medicalHistory;

    public Patient() {

    }

    public Patient(int patientId, int personId, String firstName, String lastName, long contactNo, String address, String gender, int age, String healthStatus, String medicalHistory) {
        super(personId, firstName, lastName, contactNo, address, gender, age);
        this.patientId = patientId;
        this.healthStatus = healthStatus;
        this.medicalHistory = medicalHistory;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

}
