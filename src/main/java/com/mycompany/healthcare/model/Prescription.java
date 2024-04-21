/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

/**
 *
 * @author Amandha
 */
public class Prescription {

    private int prescriptionId;
    private Patient prescribedFor;
    private Doctor prescribedBy;
    private String prescribedDate;
    private String medication;
    private String instruction;
    private String dosage;
    private String duration;

    public Prescription() {
    }

    public Prescription(int prescriptionId, Patient prescribedFor, Doctor prescribedBy, String prescribedDate, String medication, String instruction, String dosage, String duration) {
        this.prescriptionId = prescriptionId;
        this.prescribedBy = prescribedBy;
        this.prescribedFor = prescribedFor;
        this.prescribedDate = prescribedDate;
        this.medication = medication;
        this.instruction = instruction;
        this.dosage = dosage;
        this.duration = duration;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Patient getPrescribedFor() {
        return prescribedFor;
    }

    public void setPrescribedFor(Patient prescribedFor) {
        this.prescribedFor = prescribedFor;
    }

    public Doctor getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(Doctor prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(String prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
