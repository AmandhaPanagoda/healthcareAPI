/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Model class representing a Doctor entity. Extends the Person class and adds a
 * specialization field. Provides getter and setters for all fields.
 *
 * @author Amandha
 */
public class Doctor extends Person {

    @NotEmpty(message = "Specialization is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "Specialization can only contain letters")
    private String specialization;

    public Doctor() {
    }

    public Doctor(String specialization, int personId, String firstName, String lastName, long contactNo, String address, String gender, int age) {
        super(personId, firstName, lastName, contactNo, address, gender, age);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
