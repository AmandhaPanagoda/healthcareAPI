/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Represents a billing record. Contains information
 * such as bill ID, date, time, patient details, services provided, invoiced
 * amount, payment, and outstanding balance.
 *
 * @author Amandha
 */
public class Billing {

    private Integer billId;

    @NotEmpty(message = "Date is required")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Date must be in the format dd-mm-yyyy")
    private String billDate;

    @NotEmpty(message = "Time is required")
    @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Time must be in the format hh:mm:ss")
    private String billTime;

    @NotNull(message = "Patient ID is required")
    private Patient patient;
    
    @NotNull(message = "Services are required")
    private List<String> services;
    
    @NotNull(message = "Invoiced amount is required")
    private Double invoicedAmount;
    
    @NotNull(message = "Payment is required")
    private Double payment;
    
    @NotNull(message = "Outstanding balance is required")
    private Double outstandingBalance;

    public Billing() {
    }

    public Billing(int billId, String billDate, String billTime, Patient patient, List<String> services, double invoicedAmount, double payment, double outstandingBalance) {
        this.billId = billId;
        this.billDate = billDate;
        this.billTime = billTime;
        this.patient = patient;
        this.services = services;
        this.invoicedAmount = invoicedAmount;
        this.payment = payment;
        this.outstandingBalance = outstandingBalance;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public double getInvoicedAmount() {
        return invoicedAmount;
    }

    public void setInvoicedAmount(double invoicedAmount) {
        this.invoicedAmount = invoicedAmount;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

}
