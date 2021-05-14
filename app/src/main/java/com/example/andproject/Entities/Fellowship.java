package com.example.andproject.Entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fellowship {

    public String id;
    public String creatorId;
    public String webshop;
    public String category;
    public int amountNeeded;
    public String paymentMethod;
    public String deadline;
    public String pickupCoordinates;
    public String partnerId;
    public int partnerPaid;
    public int paymentApproved;
    public String receiptUrl;
    public int ownerCompleted;
    public int partnerCompleted;
    public int isCompleted; // We use this as a BIT - 1 = TRUE, 0 = FALSE

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
