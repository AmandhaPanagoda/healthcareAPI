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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Amandha
 */
public class BillingDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingDAO.class);
    private static final List<Billing> bills = new ArrayList<>();

    static {
        Patient patient = new Patient(1, 3, "Jeromy", "Osinski", 1234548548, "86869 Weissnat Light Suite 560, SF", "M", 60, "Diagnosed with ADHD", "Parkinsons patient. Who was previously admitted due to loss of memory");
        String currentDate = formatSimpleDate(new Date());
        String currentTime = formatSimpleTime(new Date());

        List<String> services = Arrays.asList("Consultation", "X-ray", "Medication");
        bills.add(new Billing(1, currentDate, currentTime, patient, services, 200.0, 150.0, 50.0));
    }

    public List<Billing> getAllBills() {
        LOGGER.info("Retrieving all bills");
        return bills;
    }

    public Billing getBillById(int billId) {
        LOGGER.info("Retrieving bill by ID " + billId);
        for (Billing bill : bills) {
            if (bill.getBillId() == billId) {
                return bill;
            }
        }
        LOGGER.info("Bill by ID " + billId + " was not found");
        return null;
    }

    public List<Billing> getBillByPatientId(int patientId) {
        LOGGER.info("Retrieving bill by Patient ID " + patientId);

        List<Billing> matchingBills = new ArrayList<>();

        for (Billing bill : bills) {
            int billPatientId = (bill.getPatient()).getPatientId();
            if (patientId == billPatientId) {
                matchingBills.add(bill);
            }
        }
        return matchingBills;
    }

    public int addBill(Billing bill) {
        LOGGER.info("Adding a new bill");

        Helper<Billing> helper = new Helper<>();
        int newBillId = helper.getNextId(bills, Billing::getBillId);

        bill.setBillId(newBillId);
        bills.add(bill);
        LOGGER.info("New bill with ID " + newBillId + " is added to bills list");

        return newBillId;
    }

    public void updateBill(Billing updatedBill) {
        LOGGER.info("Update bill");
        for (int i = 0; i < bills.size(); i++) {
            Billing bill = bills.get(i);
            if (bill.getBillId() == updatedBill.getBillId()) {
                updatedBill.setBillId(bill.getBillId());
                bills.set(i, updatedBill);
                LOGGER.info("Bill was updated. Bill ID : " + updatedBill.getBillId());
                return;
            }
        }
    }

    public boolean deleteBill(int billId) {
        boolean removed = bills.removeIf(bill -> {
            if (bill.getBillId() == billId) {
                LOGGER.info("Deleting the Bill with ID: " + billId);
                return true;
            }
            return false;
        });
        return removed;
    }

    public List<Billing> searchBills(String patientFirstName, String patientLastName, String startBillDate, String endBillDate) {
        LOGGER.info("Searching for bills in the given criteria");

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
            LOGGER.error("Error parsing dates: " + e.getMessage());
            return matchingBills;
        }

        for (Billing bill : bills) {

            Patient patient = bill.getPatient();

            // Parse appointment date string to Date object
            Date billDate = null;
            try {
                billDate = parseSimpleDate(bill.getBillDate());
            } catch (ParseException e) {
                LOGGER.error("Error parsing appointment date: " + e.getMessage());
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
