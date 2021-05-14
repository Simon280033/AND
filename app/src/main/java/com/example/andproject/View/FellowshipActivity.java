package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipViewModel;
import com.example.andproject.ViewModel.FindFellowshipsViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;

public class FellowshipActivity extends AppCompatActivity {
    private FellowshipViewModel viewModel;

    private boolean ownerOfFellowship;

    private ImageView partnerAvatarView;

    private TextView partnerNameTextView, webShopTextView, minimumAmountNeededTextView, paymentMethodTextView, paymentStatusHeader, paymentStatusTextView, receiptNameTextView;

    private Button paymentStatusButton, receiptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipViewModel.class);

        setContentView(R.layout.activity_fellowship);

        // We start out by checking whether or not the user is the owner of the fellowship
        if (viewModel.getViewFellowshipInfo().creatorId.equals(viewModel.getCurrentUserData().getValue().getUid())) {
            ownerOfFellowship = true;
        } else {
            ownerOfFellowship = false;
        }

        partnerAvatarView = findViewById(R.id.partnerAvatarView);
        partnerNameTextView = findViewById(R.id.partnerNameTextView);
        webShopTextView = findViewById(R.id.webShopTextView);
        minimumAmountNeededTextView = findViewById(R.id.minimumAmountNeededTextView);
        paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        paymentStatusHeader = findViewById(R.id.paymentStatusHeader);
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView);
        paymentStatusButton = findViewById(R.id.paymentStatusButton);
        receiptNameTextView = findViewById(R.id.receiptNameTextView);
        receiptButton = findViewById(R.id.receiptButton);

        receiptButton.setOnClickListener((View v) -> {
            receiptMethodForUser();
        });

        setPartnerInfo();
        setFirmProperties();
        setPaymentProperties();
        setReceiptProperties();
    }

    private void receiptMethodForUser() {
        if (ownerOfFellowship) {
            choosePdf();
        } else {
            try {
                downloadReceipt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadReceipt() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fellowshippers-aec83.appspot.com/");
        StorageReference storageRef = storage.getReference();

        StorageReference receiptsRef = storageRef.child("receipts/" + viewModel.getViewFellowshipInfo().id);

        File localFile = File.createTempFile("receipt", "pdf");

        receiptsRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Toast.makeText(FellowshipActivity.this, "Succesfully download receipt as .PDF! Please check your downloads.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(FellowshipActivity.this, "ERROR: Failed to download receipt! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPartnerInfo() {
        // We get the partner of the fellowship
        String partnerId = viewModel.getViewFellowshipInfo().creatorId;
        if (ownerOfFellowship) {
            partnerId = viewModel.getViewFellowshipInfo().partnerId;
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

                        partnerNameTextView.setText(displayName);
                        Glide.with(FellowshipActivity.this).load(Uri.parse(imageUrl)).into(partnerAvatarView);
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
        webShopTextView.setText(viewModel.getViewFellowshipInfo().webshop);
        minimumAmountNeededTextView.setText(viewModel.getViewFellowshipInfo().amountNeeded + " DKK");
        paymentMethodTextView.setText(viewModel.getViewFellowshipInfo().paymentMethod);
    }

    // This method handles the the payment area
    private void setPaymentProperties()  {
        if (ownerOfFellowship) {
            setPaymentPropertiesForOwner();
        } else {
            setPaymentPropertiesForPartner();
        }
    }

    // This method sets the payment properties if the user does NOT own the Fellowship
    private void setPaymentPropertiesForPartner()  {
        paymentStatusButton.setEnabled(true);
        paymentStatusButton.setText("CLICK HERE IF YOU HAVE PAID");

        paymentStatusHeader.setText("Your payment status:");
        if (viewModel.getViewFellowshipInfo().partnerPaid == 1) {
            paymentStatusButton.setText("CLICK HERE IF YOU HAVE NOT PAID");
            paymentStatusTextView.setText("Paid. Pending approval...");
            if (viewModel.getViewFellowshipInfo().paymentApproved == 1) {
                paymentStatusTextView.setText("Payment approved by partner.");
                paymentStatusButton.setEnabled(false);
            }
        } else {
            paymentStatusTextView.setText("Not paid.");
        }
    }

    // This method sets the payment properties if the user DOES own the Fellowship
    private void setPaymentPropertiesForOwner()  {
        paymentStatusButton.setEnabled(false);
        paymentStatusButton.setText("APPROVE PARTNER'S PAYMENT");

        paymentStatusHeader.setText("Partner payment status:");
        if (viewModel.getViewFellowshipInfo().partnerPaid == 1) {
            paymentStatusTextView.setText("Paid. Please approve if received...");
            if (viewModel.getViewFellowshipInfo().paymentApproved == 1) {
                paymentStatusTextView.setText("Payment approved.");
                paymentStatusButton.setEnabled(false);
            }
        } else {
            paymentStatusTextView.setText("Not paid.");
        }
    }

    private void setReceiptProperties() {
        if (ownerOfFellowship) {
            setReceiptPropertiesForOwner();
        } else {
            setReceiptPropertiesForPartner();
        }
    }

    private void setReceiptPropertiesForPartner() {
        receiptButton.setText("DOWNLOAD RECEIPT (.pdf)");
        // We check if a receipt has been uploaded
        if (viewModel.getViewFellowshipInfo().receiptUrl.equals("null")) {
            receiptNameTextView.setText("No receipt added.");
            receiptButton.setEnabled(false);
        } else {
            receiptNameTextView.setText("Receipt attached");
            receiptButton.setEnabled(true);
        }
    }

    private void setReceiptPropertiesForOwner() {
        receiptButton.setText("UPLOAD RECEIPT (.pdf)");
        // We check if a receipt has been uploaded
        if (viewModel.getViewFellowshipInfo().receiptUrl.equals("null")) {
            receiptNameTextView.setText("No receipt added.");
            receiptButton.setEnabled(true);
        } else {
            receiptNameTextView.setText("Receipt attached");
            receiptButton.setEnabled(true);
            receiptButton.setText("REPLACE RECEIPT (.pdf)");
        }
    }

    private void choosePdf() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // We will be redirected to choose pdf
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            Uri imageuri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("receipts/");
            //final String messagePushID = timestamp;
            Toast.makeText(FellowshipActivity.this, imageuri.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(viewModel.getViewFellowshipInfo().id);
            Toast.makeText(FellowshipActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // After uploading is done it progress
                        // dialog box will be dismissed
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        Toast.makeText(FellowshipActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        viewModel.getViewFellowshipInfo().setReceiptUrl(myurl);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(FellowshipActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                    setReceiptProperties();
                }
            });
        }
    }
}