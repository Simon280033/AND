package com.example.andproject.Entities;

public class Fellowship {

    public String id;
    public String creatorId;
    public String webshop;
    public String category;
    public int amountNeeded;
    public String paymentMethod;
    public String deadline;
    public String pickupCoordinates;
    public int isCompleted; // We use this as a BIT - 1 = TRUE, 0 = FALSE

    public Fellowship(String id, String creatorId, String webshop, String category, int amountNeeded, String paymentMethod, String deadline, String pickupCoordinates, int isCompleted) {
        this.id = id;
        this.creatorId = creatorId;
        this.webshop = webshop;
        this.category = category;
        this.amountNeeded = amountNeeded;
        this.paymentMethod = paymentMethod;
        this.deadline = deadline;
        this.pickupCoordinates = pickupCoordinates;
        this.isCompleted = isCompleted;
    }
}
