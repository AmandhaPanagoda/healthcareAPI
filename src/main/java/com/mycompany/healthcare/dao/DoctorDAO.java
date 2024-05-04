/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

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
        Doctor doctor1 = new Doctor("Anesthesiologist", 1, "Eric", "Anderson", 1124579548, "684 Delaware Avenue, SF", "M", 45);
        Doctor doctor2 = new Doctor("Cardiologist", 2, "Abigail", "Henderson", 1124579548, "2075 Elliott Street, NH", "F", 33);
        Doctor doctor3 = new Doctor("Neurologist", 9, "Henry", "Garcia", 1654321876, "901 Cedar Blvd, Anywhere, USA", "M", 50);

        doctors.put(1, doctor1);
        doctors.put(2, doctor2);
        doctors.put(9, doctor3);
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
        return doctors.get(doctorId);
    }

    /**
     * Adds a new doctor to the database.
     *
     * @param doctor The doctor object to add.
     */
    public void addDoctor(Doctor doctor) {
        try {
            doctors.put(doctor.getPersonId(), doctor);
            LOGGER.info("New doctor with ID {} was added to doctors list", doctor.getPersonId());
        } catch (Exception e) {
            LOGGER.error("Error adding doctor: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing doctor record in the database.
     *
     * @param updatedDoctor The updated doctor object.
     */
    public void updateDoctor(Doctor updatedDoctor) {
        try {
            doctors.put(updatedDoctor.getPersonId(), updatedDoctor);
            LOGGER.info("Doctor record was updated. Doctor ID : " + updatedDoctor.getPersonId());
        } catch (Exception e) {
            LOGGER.error("Doctor ID: " + updatedDoctor.getPersonId() + ". Error updating doctor: " + e.getMessage(), e);
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
        try {
            Doctor removedDoctor = doctors.remove(doctorId);
            if (removedDoctor != null) {
                LOGGER.info("Doctor with ID {} was successfully deleted", doctorId);
                return true;
            } else {
                LOGGER.info("Doctor with ID {} was not found", doctorId);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting Doctor with ID {}: {}", doctorId, e.getMessage());
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
