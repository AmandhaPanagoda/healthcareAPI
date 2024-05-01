/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.dao;

import com.mycompany.healthcare.helper.Helper;
import static com.mycompany.healthcare.helper.SimpleDateFormatHelper.formatSimpleDate;
import static com.mycompany.healthcare.helper.SimpleDateFormatHelper.formatSimpleTime;
import static com.mycompany.healthcare.helper.SimpleDateFormatHelper.parseSimpleDate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.mycompany.healthcare.model.Billing;
import com.mycompany.healthcare.model.Patient;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) class for managing billing Provides methods for
 * retrieving, adding, updating, and deleting billing records.
 *
 * @author Amandha
 */
public class BillingDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingDAO.class);
    private static final Map<Integer, Billing> bills = new HashMap<>();

    static {
        String currentDate = formatSimpleDate(new Date());
        String currentTime = formatSimpleTime(new Date());
        Patient patient = new Patient(3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        List<String> services = Arrays.asList("Consultation", "X-ray", "Medication");
        bills.put(1, new Billing(1, currentDate, currentTime, patient, services, 200.0, 150.0, 50.0));

        Patient patient2 = new Patient(4, "Alice", "Smith", 1124579548, "2075 Elliott Street, NH", "F", 33, "General checkup", "None");
        List<String> services2 = Arrays.asList("Consultation", "Blood test", "Prescription");
        bills.put(2, new Billing(2, currentDate, currentTime, patient2, services2, 150.0, 100.0, 50.0));

        Patient patient3 = new Patient(5, "Bob", "Johnson", 1987654321, "123 Main St, Anytown, USA", "M", 45, "Back pain", "Previous back injury");
        List<String> services3 = Arrays.asList("Consultation", "MRI", "Physiotherapy");
        bills.put(3, new Billing(3, currentDate, currentTime, patient3, services3, 300.0, 200.0, 100.0));

        List<String> services4 = Arrays.asList("MRI", "Physical therapy", "Medication");
        bills.put(2, new Billing(2, currentDate, currentTime, patient3, services3, 300.0, 200.0, 100.0));

        List<String> services5 = Arrays.asList("Blood test", "Consultation", "Medication");
        bills.put(3, new Billing(3, currentDate, currentTime, patient3, services3, 250.0, 150.0, 100.0));
    }

    /**
     * Retrieves all bills from the database.
     *
     * @return A map of all bills indexed by their IDs.
     */
    public Map<Integer, Billing> getAllBills() {
        LOGGER.info("Retrieving all bills");
        return bills;
    }

    /**
     * Retrieves a bill by its unique ID.
     *
     * @param billId The ID of the bill to retrieve.
     * @return The bill object if found, otherwise null.
     */
    public Billing getBillById(int billId) {
        LOGGER.info("Retrieving bill by ID {}", billId);
        return bills.get(billId);
    }

    /**
     * Retrieves bills associated with a patient ID.
     *
     * @param patientId The ID of the patient.
     * @return A list of bills associated with the patient.
     */
    public List<Billing> getBillByPatientId(int patientId) {
        LOGGER.info("Retrieving bills by Patient ID {}", patientId);

        return bills.values().stream()
                .filter(bill -> bill.getPatient().getPersonId() == patientId)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new bill to the database.
     *
     * @param bill The bill object to add.
     * @return The ID assigned to the new bill.
     */
    public int addBill(Billing bill) {
        try {
            Helper<Billing> helper = new Helper<>();
            int newBillId = helper.getNextId(bills); // generate the next bill ID

            bill.setBillId(newBillId); // set the new bill ID
            bills.put(newBillId, bill);
            LOGGER.info("New bill with ID {} is added to bills list", newBillId);

            return newBillId;
        } catch (Exception e) {
            LOGGER.error("Error adding bill: " + e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Updates an existing bill record in the database.
     *
     * @param updatedBill The updated bill object.
     */
    public void updateBill(Billing updatedBill) {
        try {
            bills.put(updatedBill.getBillId(), updatedBill);
            LOGGER.info("Bill was updated. Bill ID : {}", updatedBill.getBillId());
        } catch (Exception e) {
            LOGGER.error("Bill ID: " + updatedBill.getBillId()+ ". Error updating bill: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a bill record from the database.
     *
     * @param billId The ID of the bill to delete.
     * @return True if the bill was successfully deleted, otherwise false.
     */
    public boolean deleteBill(int billId) {
        Billing removedBill = bills.remove(billId);
        if (removedBill != null) {
            LOGGER.info("Bill with ID {} was successfully deleted", billId);
            return true;
        } else {
            LOGGER.info("Bill with ID {} was not found", billId);
            return false;
        }
    }

    /**
     * Searches for bills in the database based on specified criteria.
     *
     * @param patientFirstName The first name of the patient.
     * @param patientLastName The last name of the patient.
     * @param startBillDate The start date for the bill search range.
     * @param endBillDate The end date for the bill search range.
     * @return A list of matching bills.
     */
    public List<Billing> searchBills(String patientFirstName, String patientLastName, String startBillDate, String endBillDate) {
        LOGGER.info("Searching for bills with criteria - Patient First Name: {}, Patient Last Name: {}, Start Bill Date: {}, End Bill Date: {}",
                patientFirstName, patientLastName, startBillDate, endBillDate);

        List<Billing> matchingBills = new ArrayList<>();

        // Parse fromDate and toDate strings to Date objects
        Date fromDate = null;
        Date toDate = null;

        try {
            if (startBillDate != null && !startBillDate.isEmpty()) {
                fromDate = parseSimpleDate(startBillDate);
            }
            if (endBillDate != null && !endBillDate.isEmpty()) {
                toDate = parseSimpleDate(endBillDate);
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing dates: {}", e.getMessage());
            return matchingBills;
        }

        for (Billing bill : bills.values()) {

            Patient patient = bill.getPatient();

            // Parse appointment date string to Date object
            Date billDate = null;
            try {
                billDate = parseSimpleDate(bill.getBillDate());
            } catch (ParseException e) {
                LOGGER.error("Error parsing the billed date: {}", e.getMessage());
            }

            boolean matchPatientFirstName = patientFirstName == null || patientFirstName.equalsIgnoreCase(patient.getFirstName());
            boolean matchPatientLastName = patientLastName == null || patientLastName.equalsIgnoreCase(patient.getLastName());
            boolean matchDateRange = (fromDate == null || billDate.compareTo(fromDate) >= 0)
                    && (toDate == null || billDate.compareTo(toDate) <= 0);

            if (matchPatientFirstName && matchPatientLastName && matchDateRange) {
                matchingBills.add(bill);
            }
        }
        return matchingBills;
    }
}
