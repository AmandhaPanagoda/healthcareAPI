/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.healthcare.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class DoctorDAO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDAO.class);
    private static final List<Doctor> doctors = new ArrayList<>();
    
    static {
        doctors.add(new Doctor(1,"Cardiologist", 1, "Eric", "Anderson", 1234548548, "684 Delaware Avenue, SF", "M", 45));
        doctors.add(new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33));
    }
    
    public List<Doctor> getAllDoctors() {
        LOGGER.info("Retrieving all doctors");
        return doctors;
    }
    
    public Doctor getDoctorById(int doctorId) {
        LOGGER.info("Retrieving doctor by ID {0}", doctorId);
        for(Doctor doctor : doctors) {
            if(doctor.getDoctorId() == doctorId) {
                return doctor;
            }
        }
        LOGGER.info("Doctor with ID " +doctorId+ " was not found");
        return null;
    }
    
    public int addDoctor(Doctor doctor) {
        LOGGER.info("Adding a new doctor");
        
        Helper<Doctor> helper = new Helper<>();
        int newDoctorId = helper.getNextId(doctors, Doctor::getDoctorId); //get the next available doctor ID
        
        doctor.setDoctorId(newDoctorId); //set the new doctor ID
        doctors.add(doctor);
        
        return newDoctorId;
    }
    
    public void updateDoctor(Doctor updateDoctor) {
        LOGGER.info("Update doctor record");
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            if (doctor.getDoctorId() == updateDoctor.getDoctorId()) {
                doctors.set(i, updateDoctor);
                LOGGER.info("Doctor record was updated. Doctor ID : " + updateDoctor.getDoctorId());
                return;
            }
        }
    }
    
    public boolean deleteDoctor(int doctorId) {
        LOGGER.info("Deleting the doctor with ID: " + doctorId);
        boolean removed = doctors.removeIf(doctor -> {
            if (doctor.getDoctorId() == doctorId) {
                LOGGER.info("Doctor record with Doctor ID: " + doctorId + " was deleted");
                return true;
            }
            LOGGER.info("Doctor record with Doctor ID: " + doctorId + " was not found found");
            return false;
        });
        return removed;
    }
    
    public List<Doctor> searchDoctors(String firstName, String lastName, Integer minAge, Integer maxAge, String gender, String specialization) {
        LOGGER.info("Searching for people with first name: " + firstName + ", last name: " + lastName
                + ", age range: " + minAge + " - " + maxAge + ", gender: " + gender +  " and specialization: "+specialization);

        List<Doctor> matchingDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            boolean matchFirstName = firstName == null || firstName.equalsIgnoreCase(doctor.getFirstName());
            boolean matchLastName = lastName == null || lastName.equalsIgnoreCase(doctor.getLastName());
            boolean matchAge = (minAge == null || doctor.getAge() >= minAge) && (maxAge == null || doctor.getAge() <= maxAge);
            boolean matchGender = gender == null || gender.equalsIgnoreCase(doctor.getGender());
            boolean matchSpecialization = specialization == null || specialization.equalsIgnoreCase(doctor.getSpecialization());
            
            if (matchFirstName && matchLastName && matchAge && matchGender && matchSpecialization) {
                matchingDoctors.add(doctor);
            }
        }
        return matchingDoctors;
    }

}
