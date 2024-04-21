/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.model;

import java.util.List;

/**
 *
 * @author Amandha
 */
public class Billing {
    private int billId;
    private String billDate;
    private String billTime;
    private Patient patient;
    private List<String> services;
    private double invoicedAmount;
    private double payment;
    private double outstandingBalance;

    public Billing() {
    }

    public Billing(int billId, String billDate,String billTime, Patient patient, List<String> services, double invoicedAmount, double payment, double outstandingBalance) {
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
