/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

/**
 *
 * @author Amandha
 */
public class MedicalRecord {
    private int medicalRecordId;
    private Patient patient;
    private String allergies;
    private String diagnosis;
    private String treatment;
    private String bloodGroup;

    public MedicalRecord() {
    }

    public MedicalRecord(int medicalRecordId, Patient patient, String allergies, String diagnosis, String treatment, String bloodGroup) {
        this. medicalRecordId = medicalRecordId;
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

