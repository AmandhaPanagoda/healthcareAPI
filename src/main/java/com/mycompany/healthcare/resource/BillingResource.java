/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.resource;

import com.mycompany.healthcare.dao.BillingDAO;
import com.mycompany.healthcare.exception.ModelIdMismatchException;
import com.mycompany.healthcare.exception.ResourceNotFoundException;
import com.mycompany.healthcare.helper.ValidationHelper;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.healthcare.model.Billing;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class for managing billing records in the healthcare system.
 * Provides endpoints for retrieving, adding, updating, and deleting billing
 * records.
 *
 * @author Amandha
 */
@Path("bills")
public class BillingResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingResource.class);
    private final BillingDAO billingDAO = new BillingDAO();

    /**
     * Retrieves all billing records.
     *
     * @return A collection of billing records.
     * @throws ResourceNotFoundException if no records were found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Billing> getAllBills() {
        if (!billingDAO.getAllBills().isEmpty()) {
            return billingDAO.getAllBills().values();
        } else {
            throw new ResourceNotFoundException("No records were found");
        }
    }

    /**
     * Retrieves a billing record by its ID.
     *
     * @param billId The ID of the billing record to retrieve.
     * @return The billing record.
     * @throws ResourceNotFoundException if the record with the specified ID was
     * not found.
     */
    @GET
    @Path("/{billId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Billing getAppointmentById(@PathParam("billId") int billId) {
        LOGGER.info("Getting the bill by ID: " + billId);
        Billing bill = billingDAO.getBillById(billId);
        if (bill != null) {
            return bill;
        } else {
            throw new ResourceNotFoundException("Bill with ID " + billId + " was not found");
        }
    }

    /**
     * Adds a new billing record.
     *
     * @param bill The billing record to add.
     * @return A response indicating the success of the operation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBill(Billing bill) {
        LOGGER.info("Adding a new bill");

        // Validate the bill object
        String validationError = ValidationHelper.validate(bill);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        int newBillId = billingDAO.addBill(bill);
        return Response.status(Response.Status.CREATED).entity("New bill with ID: " + newBillId + " was added successfully").build();
    }

    /**
     * Updates an existing billing record.
     *
     * @param billId The ID of the billing record to update.
     * @param updatedBill The updated billing record.
     * @return A response indicating the success of the operation.
     */
    @PUT
    @Path("/{billId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBill(@PathParam("billId") int billId, Billing updatedBill) {
        if (billId != updatedBill.getBillId()) {
            LOGGER.info("URL parameter bill ID and the passed bill ID do not match");
            throw new ModelIdMismatchException("The passed bill IDs do not match");
        }
        LOGGER.info("Updating bill with ID: " + billId);
        Billing existingBill = billingDAO.getBillById(billId);

        if (existingBill != null) {
            updatedBill.setBillId(billId);
            billingDAO.updateBill(updatedBill);
            LOGGER.info("Bill record was updated. Updated Bill ID: " + billId);
            return Response.status(Response.Status.OK).entity("Bill with ID " + billId + " was updated successfully").build();
        } else {
            LOGGER.error("Bill ID" + billId + " was not found");
            throw new ResourceNotFoundException("Bill with ID " + billId + " was not found");
        }
    }

    /**
     * Deletes a billing record.
     *
     * @param billId The ID of the billing record to delete.
     * @return A response indicating the success of the operation.
     */
    @DELETE
    @Path("/{billId}")
    public Response deleteBill(@PathParam("billId") int billId) {
        boolean removed = billingDAO.deleteBill(billId);
        if (removed) {
            return Response.status(Response.Status.OK).entity("Bill with ID " + billId + " was deleted successfully").build();
        } else {
            throw new ResourceNotFoundException("Bill with ID " + billId + " was not found");
        }
    }

    /**
     * Searches for billing records based on specified criteria.
     *
     * @param patientFirstName The first name of the patient.
     * @param patientLastName The last name of the patient.
     * @param startBillDate The start date of the billing record.
     * @param endBillDate The end date of the billing record.
     * @return A response containing the matching billing records.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBills(
            @QueryParam("patientFirstName") String patientFirstName,
            @QueryParam("patientLastName") String patientLastName,
            @QueryParam("startBillDate") String startBillDate,
            @QueryParam("endBillDate") String endBillDate) {

        LOGGER.info("Searching for bills with the given criteria");

        if ((patientFirstName == null || patientFirstName.isEmpty())
                && (patientLastName == null || patientLastName.isEmpty())
                && (startBillDate == null || startBillDate.isEmpty())
                && (endBillDate == null || endBillDate.isEmpty())) {
            return Response.ok(getAllBills()).build();
        }

        try {
            List<Billing> matchingBills = billingDAO.searchBills(
                    patientFirstName, patientLastName, startBillDate, endBillDate);
            if (!matchingBills.isEmpty()) {
                return Response.ok(matchingBills).build();
            } else {
                throw new ResourceNotFoundException("No bills found with the given search criteria");
            }
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
