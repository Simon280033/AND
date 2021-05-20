package com.example.andproject.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.ProfileComment;
import com.example.andproject.Entities.Report;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.example.andproject.View.FellowshipActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
// This viewmodel determines what is being shown on the fellowship view
public class FellowshipViewModel extends AndroidViewModel {
    private final Model model;

    // TextView contents
    private MutableLiveData<String> partnerAvatar;
    private MutableLiveData<String> partnerName;
    private MutableLiveData<String> webShop;
    private MutableLiveData<String> minimumAmount;
    private MutableLiveData<String> paymentMethod;
    private MutableLiveData<String> paymentStatus;
    private MutableLiveData<String> receiptStatus;
    private MutableLiveData<String> completionStatus;

    // Button properties
    private MutableLiveData<String> paymentStatusButtonText;
    private MutableLiveData<Boolean> paymentStatusButtonEnabled;
    private MutableLiveData<String> receiptButtonText;
    private MutableLiveData<Boolean> receiptButtonEnabled;
    private MutableLiveData<String> markAsDoneButtonText;
    private MutableLiveData<Boolean> markAsDoneButtonEnabled;

    // Header properties
    private MutableLiveData<String> paymentStatusHeaderText;

    // Status properties
    private MutableLiveData<Boolean> downloadSuccessful;
    private MutableLiveData<Boolean> fellowshipDone;

    private boolean ownerOfFellowship;

    public FellowshipViewModel(Application app){
        super(app);
        model = Model.getInstance(app);

        // We start out by checking whether or not the user is the owner of the fellowship
        if (model.getViewFellowshipInfo().getCreatorId().equals(model.getCurrentUserData().getValue().getUid())) {
            ownerOfFellowship = true;
        } else {
            ownerOfFellowship = false;
        }
    }

    public void setChatReceiver() {
        model.setChatReceiver(model.getFellowshipPartner());
    }

    public void setViewProfileOf() {
        model.setViewProfileOf(model.getFellowshipPartner());
    }

    public void submitComment(String message) {
        String senderId = model.getCurrentUserData().getValue().getUid();
        String senderName = model.getThisUser().getDisplayName();
        String senderImageUrl = model.getThisUser().getImageUrl();
        String receiverId = model.getFellowshipPartner().getId();

        ProfileComment pc = new ProfileComment(senderId, senderName, senderImageUrl, receiverId, message);
        FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("profileComments").child(model.getFellowshipPartner().getId())
                .setValue(pc);
    }


    public void setProperties() {
        setPartnerInfo();
        setFirmProperties();
        setPaymentProperties();
        setReceiptProperties();
        setMarkAsDoneProperties();
    }

    public void paymentAction() {
        if (ownerOfFellowship) {
            if (model.getViewFellowshipInfo().getPartnerPaid() == 1 && model.getViewFellowshipInfo().getPaymentApproved() == 0) {
                model.getViewFellowshipInfo().approvePayment();
            }
        } else {
            if (model.getViewFellowshipInfo().getPartnerPaid() == 0) {
                model.getViewFellowshipInfo().claimPayment();
            } else if (model.getViewFellowshipInfo().getPartnerPaid() == 1) {
                model.getViewFellowshipInfo().retractPaymentClaim();
            }
        }
        setPaymentProperties();
    }

    public void markAsDoneMethod(boolean markAsDone) {
        int bit = 0;
        if (markAsDone) {
            bit = 1;
        }

        if (ownerOfFellowship) {
            model.getViewFellowshipInfo().setOwnerCompletionStatus(bit);
        } else {
            model.getViewFellowshipInfo().setPartnerCompletionStatus(bit);
        }
        setMarkAsDoneProperties();
    }

    private void setMarkAsDoneProperties() {
        if (model.getViewFellowshipInfo().getPartnerPaid() == 1 && model.getViewFellowshipInfo().getPaymentApproved() == 1) {
            markAsDoneButtonEnabled.setValue(true);
        } else {
            markAsDoneButtonEnabled.setValue(false);
        }
        if (model.getViewFellowshipInfo().getPartnerCompleted() == 1 && model.getViewFellowshipInfo().getOwnerCompleted() == 1) {
            completionStatus.setValue("Both parties have marked the Fellowship as completed!");
            markAsDoneButtonEnabled.setValue(false);

            model.incrementCompletionCounterForBothUsers(model.getViewFellowshipInfo().getCreatorId(), model.getViewFellowshipInfo().getPartnerId());

            model.getViewFellowshipInfo().setFellowshipAsCompleted();

            fellowshipDone.setValue(true);
        }
        if (ownerOfFellowship) {
            setMarkAsDonePropertiesForOwner();
        } else {
            setMarkAsDonePropertiesForPartner();
        }
    }

    private void setMarkAsDonePropertiesForOwner() {
        if (model.getViewFellowshipInfo().getOwnerCompleted() == 1) {
            completionStatus.setValue("You have marked the Fellowship as completed. Partner pending...");
            markAsDoneButtonText.setValue("RETRACT COMPLETION");
        }
        if (model.getViewFellowshipInfo().getPartnerCompleted() == 1) {
            completionStatus.setValue("Partner has marked the Fellowship as completed. Response pending...");
            markAsDoneButtonText.setValue("MARK FELLOWSHIP AS COMPLETED");
        }
    }

    private void setMarkAsDonePropertiesForPartner() {
        if (model.getViewFellowshipInfo().getPartnerCompleted() == 1) {
            completionStatus.setValue("You have marked the Fellowship as completed. Partner pending...");
            markAsDoneButtonText.setValue("RETRACT COMPLETION");
        }
        if (model.getViewFellowshipInfo().getOwnerCompleted() == 1) {
            completionStatus.setValue("Partner has marked the Fellowship as completed. Response pending...");
            markAsDoneButtonText.setValue("MARK FELLOWSHIP AS COMPLETED");
        }
    }

    public void downloadReceipt() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fellowshippers-aec83.appspot.com/");
        StorageReference storageRef = storage.getReference();

        StorageReference receiptsRef = storageRef.child("receipts/" + model.getViewFellowshipInfo().getId());

        File localFile = File.createTempFile("receipt", "pdf");

        receiptsRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                // Listen for string change and toast when it happens!
                //Toast.makeText(FellowshipActivity.this, "Succesfully download receipt as .PDF! Please check your downloads.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //Toast.makeText(FellowshipActivity.this, "ERROR: Failed to download receipt! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setReceiptProperties() {
        if (ownerOfFellowship) {
            setReceiptPropertiesForOwner();
        } else {
            setReceiptPropertiesForPartner();
        }
    }

    private void setReceiptPropertiesForPartner() {
        receiptButtonText.setValue("DOWNLOAD RECEIPT (.pdf)");
        // We check if a receipt has been uploaded
        if (model.getViewFellowshipInfo().getReceiptUrl().equals("null")) {
            receiptStatus.setValue("No receipt added.");
            receiptButtonEnabled.setValue(false);
        } else {
            receiptStatus.setValue("Receipt attached");
            receiptButtonEnabled.setValue(true);
        }
    }

    private void setReceiptPropertiesForOwner() {
        receiptButtonText.setValue("UPLOAD RECEIPT (.pdf)");
        // We check if a receipt has been uploaded
        if (model.getViewFellowshipInfo().getReceiptUrl().equals("null")) {
            receiptStatus.setValue("No receipt added.");
            receiptButtonEnabled.setValue(true);
        } else {
            receiptStatus.setValue("Receipt attached");
            receiptButtonEnabled.setValue(true);
            receiptButtonText.setValue("REPLACE RECEIPT (.pdf)");
        }
    }

    private void setPartnerInfo() {
        // We get the partner of the fellowship
        String partnerId = model.getViewFellowshipInfo().getCreatorId();
        if (ownerOfFellowship) {
            partnerId = model.getViewFellowshipInfo().getPartnerId();
        }
        // We get their displayName and avatar URL
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users").orderByChild("id").equalTo(partnerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");
                        String email = ((HashMap<String, String>) issue.getValue()).get("email");
                        String userId = ((HashMap<String, String>) issue.getValue()).get("id");

                        partnerName.setValue(displayName);
                        partnerAvatar.setValue(imageUrl);

                        User fellowshipPartner = new User(userId, displayName, imageUrl, email);

                        model.setFellowshipPartner(fellowshipPartner);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // This method sets the properties that are the same regardless of whether the user is the owner or not
    private void setFirmProperties() {
        webShop.setValue(model.getViewFellowshipInfo().getWebshop());
        minimumAmount.setValue(model.getViewFellowshipInfo().getAmountNeeded() + " DKK");
        paymentMethod.setValue(model.getViewFellowshipInfo().getPaymentMethod());
    }

    // This method handles the the payment area
    private void setPaymentProperties()  {
        if (ownerOfFellowship) {
            setPaymentPropertiesForOwner();
        } else {
            setPaymentPropertiesForPartner();
        }
        setMarkAsDoneProperties();
    }

    // This method sets the payment properties if the user does NOT own the Fellowship
    private void setPaymentPropertiesForPartner()  {
        paymentStatusButtonEnabled.setValue(true);
        paymentStatusButtonText.setValue("CLICK HERE IF YOU HAVE PAID");

        paymentStatusHeaderText.setValue("Your payment status:");
        if (model.getViewFellowshipInfo().getPartnerPaid() == 1) {
            paymentStatusButtonText.setValue("RETRACT PAYMENT CLAIM");
            paymentStatus.setValue("Paid. Pending approval...");
            if (model.getViewFellowshipInfo().getPaymentApproved() == 1) {
                paymentStatus.setValue("Payment approved by partner.");
                paymentStatusButtonEnabled.setValue(false);
            }
        } else {
            paymentStatus.setValue("Not paid.");
        }
    }

    // This method sets the payment properties if the user DOES own the Fellowship
    private void setPaymentPropertiesForOwner()  {
        paymentStatusButtonEnabled.setValue(false);
        paymentStatusButtonText.setValue("APPROVE PARTNER'S PAYMENT");

        paymentStatusHeaderText.setValue("Partner payment status:");
        if (model.getViewFellowshipInfo().getPartnerPaid() == 1) {
            paymentStatus.setValue("Paid. Please approve if received...");
            paymentStatusButtonEnabled.setValue(true);
            if (model.getViewFellowshipInfo().getPaymentApproved() == 1) {
                paymentStatus.setValue("Payment approved.");
                paymentStatusButtonEnabled.setValue(false);
            }
        } else {
            paymentStatus.setValue("Not paid.");
        }
    }

    public MutableLiveData<Boolean> getFellowshipDone() {
        if (fellowshipDone == null) {
            fellowshipDone = new MutableLiveData<Boolean>();
        }
        return fellowshipDone;
    }

    public MutableLiveData<Boolean> getMarkAsDoneButtonEnabled() {
        if (markAsDoneButtonEnabled == null) {
            markAsDoneButtonEnabled = new MutableLiveData<Boolean>();
        }
        return markAsDoneButtonEnabled;
    }

    public MutableLiveData<String> getMarkAsDoneButtonText() {
        if (markAsDoneButtonText == null) {
            markAsDoneButtonText = new MutableLiveData<String>();
        }
        return markAsDoneButtonText;
    }

    public MutableLiveData<Boolean> getDownloadSuccessful() {
        if (downloadSuccessful == null) {
            downloadSuccessful = new MutableLiveData<Boolean>();
        }
        return downloadSuccessful;
    }

    public MutableLiveData<String> getReceiptButtonText() {
        if (receiptButtonText == null) {
            receiptButtonText = new MutableLiveData<String>();
        }
        return receiptButtonText;
    }

    public MutableLiveData<Boolean> getReceiptButtonEnabled() {
        if (receiptButtonEnabled == null) {
            receiptButtonEnabled = new MutableLiveData<Boolean>();
        }
        return receiptButtonEnabled;
    }

    public MutableLiveData<String> getPaymentStatusHeaderText() {
        if (paymentStatusHeaderText == null) {
            paymentStatusHeaderText = new MutableLiveData<String>();
        }
        return paymentStatusHeaderText;
    }

    public MutableLiveData<Boolean> getPaymentStatusButtonEnabled() {
        if (paymentStatusButtonEnabled == null) {
            paymentStatusButtonEnabled = new MutableLiveData<Boolean>();
        }
        return paymentStatusButtonEnabled;
    }

    public MutableLiveData<String> getPaymentStatusButtonText() {
        if (paymentStatusButtonText == null) {
            paymentStatusButtonText = new MutableLiveData<String>();
        }
        return paymentStatusButtonText;
    }

    public MutableLiveData<String> getPartnerAvatar() {
        if (partnerAvatar == null) {
            partnerAvatar = new MutableLiveData<String>();
        }
        return partnerAvatar;
    }

    public MutableLiveData<String> getPartnerName() {
        if (partnerName == null) {
            partnerName = new MutableLiveData<String>();
        }
        return partnerName;
    }

    public MutableLiveData<String> getWebShop() {
        if (webShop == null) {
            webShop = new MutableLiveData<String>();
        }
        return webShop;
    }

    public MutableLiveData<String> getMinimumAmount() {
        if (minimumAmount == null) {
            minimumAmount = new MutableLiveData<String>();
        }
        return minimumAmount;
    }

    public MutableLiveData<String> getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = new MutableLiveData<String>();
        }
        return paymentMethod;
    }

    public MutableLiveData<String> getPaymentStatus() {
        if (paymentStatus == null) {
            paymentStatus = new MutableLiveData<String>();
        }
        return paymentStatus;
    }

    public MutableLiveData<String> getReceiptStatus() {
        if (receiptStatus == null) {
            receiptStatus = new MutableLiveData<String>();
        }
        return receiptStatus;
    }

    public MutableLiveData<String> getCompletionStatus() {
        if (completionStatus == null) {
            completionStatus = new MutableLiveData<String>();
        }
        return completionStatus;
    }

    public void incrementCompletionCounterForBothUsers(String ownerId, String partnerId) {
        model.incrementCompletionCounterForBothUsers(ownerId, partnerId);
    }

    public Fellowship getViewFellowshipInfo() {
        return model.getViewFellowshipInfo();
    }

    public User getViewProfileOf() {
        return model.getViewProfileOf();
    }

    public boolean isOwnProfile() {
        if (model.getViewProfileOf().getId().equals(model.getCurrentUserData().getValue().getUid())) {
            return true;
        } else {
            return false;
        }
    }

    public LiveData<FirebaseUser> getCurrentUserData(){
        return model.getCurrentUserData();
    }
}
