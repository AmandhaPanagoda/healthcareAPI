/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Represents a medical record containing information about a patient's medical history,
 * including allergies, diagnosis, treatment, and blood group.
 * 
 * @author Amandha
 */
public class MedicalRecord {

    private Integer medicalRecordId;

    @NotNull(message = "Patient ID is required")
    private Patient patient;

    private String allergies;
    
    @NotNull(message = "Diagnosis is required")
    private String diagnosis;
    
    @NotNull(message = "Treatement is required")
    private String treatment;

    @NotEmpty(message = "Blood Group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid Blood Group")
    private String bloodGroup;

    public MedicalRecord() {
    }

    public MedicalRecord(int medicalRecordId, Patient patient, String allergies, String diagnosis, String treatment, String bloodGroup) {
        this.medicalRecordId = medicalRecordId;
        this.patient = patient;
        this.allergies = allergies;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.bloodGroup = bloodGroup;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
