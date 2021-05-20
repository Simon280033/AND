package com.example.andproject.Entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// This class holds the info for a FellowShip.
// On top of that, it also contains methods to update its equivalent in the Realtime database, when data is updated on the local object
public class Fellowship {

    private String id;
    private String creatorId;
    private String webshop;
    private String category;
    private int amountNeeded;
    private String paymentMethod;
    private String deadline;
    private String pickupCoordinates;
    private String partnerId;
    private int partnerPaid;
    private int paymentApproved;
    private String receiptUrl;
    private int ownerCompleted;
    private int partnerCompleted;
    private int isCompleted; // We use this as a BIT - 1 = TRUE, 0 = FALSE

    public Fellowship(String id, String creatorId, String webshop, String category, int amountNeeded, String paymentMethod, String deadline, String pickupCoordinates, String partnerId, int partnerPaid, int paymentApproved, String receiptUrl, int ownerCompleted, int partnerCompleted, int isCompleted) {
        this.id = id;
        this.creatorId = creatorId;
        this.webshop = webshop;
        this.category = category;
        this.amountNeeded = amountNeeded;
        this.paymentMethod = paymentMethod;
        this.deadline = deadline;
        this.pickupCoordinates = pickupCoordinates;
        this.partnerId = partnerId;
        this.partnerPaid = partnerPaid;
        this.paymentApproved = paymentApproved;
        this.receiptUrl = receiptUrl;
        this.ownerCompleted = ownerCompleted;
        this.partnerCompleted = partnerCompleted;
        this.isCompleted = isCompleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getWebshop() {
        return webshop;
    }

    public void setWebshop(String webshop) {
        this.webshop = webshop;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public void setAmountNeeded(int amountNeeded) {
        this.amountNeeded = amountNeeded;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPickupCoordinates() {
        return pickupCoordinates;
    }

    public void setPickupCoordinates(String pickupCoordinates) {
        this.pickupCoordinates = pickupCoordinates;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public int getPartnerPaid() {
        return partnerPaid;
    }

    public void setPartnerPaid(int partnerPaid) {
        this.partnerPaid = partnerPaid;
    }

    public int getPaymentApproved() {
        return paymentApproved;
    }

    public void setPaymentApproved(int paymentApproved) {
        this.paymentApproved = paymentApproved;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public int getOwnerCompleted() {
        return ownerCompleted;
    }

    public void setOwnerCompleted(int ownerCompleted) {
        this.ownerCompleted = ownerCompleted;
    }

    public int getPartnerCompleted() {
        return partnerCompleted;
    }

    public void setPartnerCompleted(int partnerCompleted) {
        this.partnerCompleted = partnerCompleted;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    // The following methods update the Realtime database entry
    public void setFellowshipAsCompleted() {
        this.isCompleted = 1;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("isCompleted").setValue(1);
    }

    public void setOwnerCompletionStatus(int completed) {
        this.ownerCompleted = completed;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("ownerCompleted").setValue(completed);
    }

    public void setPartnerCompletionStatus(int completed) {
        this.partnerCompleted = completed;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("partnerCompleted").setValue(completed);
    }

    public void approvePayment() {
        this.paymentApproved = 1;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("paymentApproved").setValue(1);
    }

    public void claimPayment() {
        this.partnerPaid = 1;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("partnerPaid").setValue(1);
    }

    public void retractPaymentClaim() {
        this.partnerPaid = 0;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("partnerPaid").setValue(0);
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("receiptUrl").setValue(receiptUrl);
    }

    private void markAsComplete() {
        this.isCompleted = 1;

        // We update it in the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(this.id);
        mDatabase.child("isCompleted").setValue(1);
    }
}
