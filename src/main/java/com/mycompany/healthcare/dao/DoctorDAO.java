/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import com.mycompany.healthcare.helper.ObjectPatcherHelper;
import com.mycompany.healthcare.model.Doctor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) for managing Doctor objects. This class provides
 * methods for retrieving, adding, updating, and deleting Doctor objects.
 *
 * @author Amandha
 */
public class DoctorDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDAO.class);
    private static final Map<Integer, Doctor> doctors = new HashMap<>();

    // Static block to initialize some sample doctor records
    static {
        doctors.put(1, new Doctor(1, "Cardiologist", 1, "Eric", "Anderson", 1234548548, "684 Delaware Avenue, SF", "M", 45));
        doctors.put(2, new Doctor(2, "Neurologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33));
    }

    /**
     * Retrieves all doctors from the database.
     *
     * @return A map of all doctors indexed by their IDs.
     */
    public Map<Integer, Doctor> getAllDoctors() {
        LOGGER.info("Retrieving all doctors");
        return doctors;
    }

    /**
     * Retrieves a doctor by their unique ID.
     *
     * @param doctorId The ID of the doctor to retrieve.
     * @return The doctor object if found, otherwise null.
     */
    public Doctor getDoctorById(int doctorId) {
        LOGGER.info("Retrieving doctor by ID: {}", doctorId);
        Doctor doctor = doctors.get(doctorId);
        if (doctor == null) {
            LOGGER.info("Doctor with ID {} was not found", doctorId);
        }
        return doctor;
    }

    /**
     * Adds a new doctor to the database.
     *
     * @param doctor The doctor object to add.
     * @return The ID assigned to the new doctor.
     */
    public int addDoctor(Doctor doctor) {
        LOGGER.info("Adding a new doctor");

        Helper<Doctor> helper = new Helper<>();
        int newDoctorId = helper.getNextId(doctors); //get the next available doctor ID

        doctor.setDoctorId(newDoctorId); //set the new doctor ID
        doctors.put(newDoctorId, doctor);

        return newDoctorId;
    }

    /**
     * Updates an existing doctor record in the database.
     *
     * @param updatedDoctor The updated doctor object.
     */
    public void updateDoctor(Doctor updatedDoctor) {
        LOGGER.info("Updating doctor record");
        Doctor existingDoctor = doctors.get(updatedDoctor.getDoctorId());
        if (existingDoctor != null) {
            doctors.put(updatedDoctor.getDoctorId(), updatedDoctor);
            LOGGER.info("Doctor record was updated. Doctor ID : {}", updatedDoctor.getDoctorId());
        } else {
            LOGGER.info("Doctor record with ID {} was not found", updatedDoctor.getDoctorId());
        }
    }

    /**
     * Partially updates an existing doctor record by applying non-null fields
     * from the partial updated doctor object.
     *
     * @param existingDoctor The existing doctor object to update.
     * @param partialUpdatedDoctor The partial updated doctor object containing
     * the new values.
     */
    public void partialUpdateDoctor(Doctor existingDoctor, Doctor partialUpdatedDoctor) {
        try {
            LOGGER.info("Updating the doctor record");
            ObjectPatcherHelper.objectPatcher(existingDoctor, partialUpdatedDoctor);
        } catch (IllegalAccessException e) {
            LOGGER.error("An error occured: " + e.getMessage());
        }
    }

    /**
     * Deletes a doctor record from the database.
     *
     * @param doctorId The ID of the doctor to delete.
     * @return True if the doctor was successfully deleted, otherwise false.
     */
    public boolean deleteDoctor(int doctorId) {
        LOGGER.info("Deleting the doctor with ID: {}", doctorId);
        Doctor removedDoctor = doctors.remove(doctorId);
        if (removedDoctor != null) {
            LOGGER.info("Doctor record with ID {} was deleted", doctorId);
            return true;
        } else {
            LOGGER.info("Doctor record with ID {} was not found", doctorId);
            return false;
        }
    }

    /**
     * Searches for doctors in the database based on specified criteria.
     *
     * @param firstName The first name of the doctor.
     * @param lastName The last name of the doctor.
     * @param minAge The minimum age of the doctor.
     * @param maxAge The maximum age of the doctor.
     * @param gender The gender of the doctor.
     * @param specialization The specialization of the doctor.
     * @return A list of matching doctors indexed by their IDs.
     */
    public List<Doctor> searchDoctors(String firstName, String lastName, Integer minAge, Integer maxAge, String gender, String specialization) {
        LOGGER.info("Searching for doctors with first name: {}, last name: {}, age range: {} - {}, gender: {}, and specialization: {}",
                firstName, lastName, minAge, maxAge, gender, specialization);

        List<Doctor> matchingDoctors = new ArrayList<>();
        for (Doctor doctor : doctors.values()) {
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
