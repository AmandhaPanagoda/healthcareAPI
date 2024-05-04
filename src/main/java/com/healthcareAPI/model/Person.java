/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Represents a person with attributes such as first name, last name, age, gender, contact number, and address.
 * Provides getter and setters for these attributes.
 * 
 * @author Amandha
 */
public class Person {
    
    private Integer personId;

    @NotEmpty(message = "First name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "First name can only contain letters")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "Last name can only contain letters")
    private String lastName;

    @NotNull(message = "Age is required")
    private Integer age;

    @NotEmpty(message = "Gender is required")
    private String gender;

    @NotNull(message = "Contact Number is required")
    @Digits(integer = 10, fraction = 0, message = "Phone number must be 10 digits")
    private Long contactNo;

    @NotEmpty(message = "Address is required")
    private String address;

    public Person() {
    }

    public Person(int personId, String firstName, String lastName, long contactNo, String address, String gender, Integer age) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.address = address;
        this.gender = gender;
        this.age = age;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personID) {
        this.personId = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getContactNo() {
        return contactNo;
    }

    public void setContactNo(Long contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
