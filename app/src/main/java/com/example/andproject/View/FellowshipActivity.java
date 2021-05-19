package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andproject.Entities.User;
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
import java.util.ArrayList;
import java.util.HashMap;

public class FellowshipActivity extends AppCompatActivity {
    private FellowshipViewModel viewModel;

    private boolean ownerOfFellowship;

    private ImageView partnerAvatarView;

    private TextView partnerNameTextView, webShopTextView, minimumAmountNeededTextView, paymentMethodTextView, paymentStatusHeader, paymentStatusTextView, receiptNameTextView, completionStatusTextView;

    private Button paymentStatusButton, receiptButton, markAsDoneButton, openChatButton;

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

        findViews();

        setButtonActions();

        bindUiElements();

        viewModel.setProperties();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the avatar
        final Observer<String> avatarUrlObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                Glide.with(FellowshipActivity.this).load(Uri.parse(newValue)).apply(RequestOptions.circleCropTransform()).into(partnerAvatarView);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPartnerAvatar().observe(this, avatarUrlObserver);

        // We bind the partnername
        final Observer<String> partnerNameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                partnerNameTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPartnerName().observe(this, partnerNameObserver);

        // We bind the web shop
        final Observer<String> webShopObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                webShopTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getWebShop().observe(this, webShopObserver);

        // We bind the minimum amount needed
        final Observer<String> amountObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                minimumAmountNeededTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getMinimumAmount().observe(this, amountObserver);

        // We bind the payment method
        final Observer<String> paymentMethodObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                paymentMethodTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentMethod().observe(this, paymentMethodObserver);

        // We bind the payment header text
        final Observer<String> paymentHeaderObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                paymentStatusHeader.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentStatusHeaderText().observe(this, paymentHeaderObserver);

        // We bind the payment status
        final Observer<String> paymentStatusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                paymentStatusTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentStatus().observe(this, paymentStatusObserver);

        // We bind the payment status button text
        final Observer<String> paymentStatusButtonTextObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                paymentStatusButton.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentStatusButtonText().observe(this, paymentStatusButtonTextObserver);

        // We bind the payment status button availability
        final Observer<Boolean> paymentStatusButtonAvailabilityObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean newValue) {
                paymentStatusButton.setEnabled(newValue);
                if (newValue) {
                    paymentStatusButton.setAlpha(1f);
                } else {
                    paymentStatusButton.setAlpha(.5f);
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentStatusButtonEnabled().observe(this, paymentStatusButtonAvailabilityObserver);

        // We bind the receipt name/status
        final Observer<String> receiptStatusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                receiptNameTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getReceiptStatus().observe(this, receiptStatusObserver);

        // We bind the receipt button text
        final Observer<String> receiptButtonTextObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                receiptButton.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getReceiptButtonText().observe(this, receiptButtonTextObserver);

        // We bind the receipt button availability
        final Observer<Boolean> receiptButtonAvailabilityObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean newValue) {
                receiptButton.setEnabled(newValue);
                if (newValue) {
                    receiptButton.setAlpha(1f);
                } else {
                    receiptButton.setAlpha(.5f);
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getReceiptButtonEnabled().observe(this, receiptButtonAvailabilityObserver);

        // We bind the completion status
        final Observer<String> completionStatusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                completionStatusTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getCompletionStatus().observe(this, completionStatusObserver);

        // We bind the mark as done button text
        final Observer<String> markAsDoneButtonTextObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                markAsDoneButton.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getMarkAsDoneButtonText().observe(this, markAsDoneButtonTextObserver);

        // We bind the mark as done button availability
        final Observer<Boolean> markAsDoneButtonAvailabilityObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean newValue) {
                markAsDoneButton.setEnabled(newValue);
                if (newValue) {
                    markAsDoneButton.setAlpha(1f);
                } else {
                    markAsDoneButton.setAlpha(.5f);
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getMarkAsDoneButtonEnabled().observe(this, markAsDoneButtonAvailabilityObserver);

        // We bind the download status
        final Observer<Boolean> downloadStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean downloadSuccessful) {
                if (downloadSuccessful) {
                    Toast.makeText(FellowshipActivity.this, "Succesfully download receipt as .PDF! Please check your downloads.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FellowshipActivity.this, "ERROR: Failed to download receipt! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDownloadSuccessful().observe(this, downloadStatusObserver);

        // We bind the fellowship completion status
        final Observer<Boolean> fellowshipCompletionStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean fellowshipCompleted) {
                if (fellowshipCompleted) {
                    Toast.makeText(FellowshipActivity.this, "Fellowship completed!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getFellowshipDone().observe(this, fellowshipCompletionStatusObserver);
    }

    private void setButtonActions() {
        openChatButton.setOnClickListener((View v) -> {
            viewModel.setChatReceiver();
            goToChat();
        });

        receiptButton.setOnClickListener((View v) -> {
            receiptMethodForUser();
        });

        markAsDoneButton.setOnClickListener((View v) -> {
            markAsDoneMethodForUser();
        });

        paymentStatusButton.setOnClickListener((View v) -> {
            paymentButtonMethod();
        });

        partnerAvatarView.setOnClickListener((View v) -> {
            viewModel.setViewProfileOf();
            goToProfileView();
        });
    }

    private void findViews() {
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
        markAsDoneButton = findViewById(R.id.markAsDoneButton);
        completionStatusTextView = findViewById(R.id.completionStatusTextView);
        openChatButton = findViewById(R.id.openChatButton);
    }

    private void goToChat() {
        startActivity(new Intent(this, ChatActivity.class));
    }

    private void goToProfileView() {
        startActivity(new Intent(this, ProfileViewActivity.class));
    }

    private void paymentButtonMethod() {
        viewModel.paymentAction();
    }

    private void markAsDoneMethodForUser() {
        if (ownerOfFellowship) {
            if (viewModel.getViewFellowshipInfo().ownerCompleted == 0) {
                showOptionsForMarkingAsDone(true);
            } else {
                showOptionsForMarkingAsDone(false);
            }
        } else {
            if (viewModel.getViewFellowshipInfo().partnerCompleted == 0) {
                showOptionsForMarkingAsDone(true);
            } else {
                showOptionsForMarkingAsDone(false);
            }
        }
    }

    private void showOptionsForMarkingAsDone(boolean markAsDone) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        if (markAsDone) {
            alertDialog.setTitle("Mark Fellowship as done");
            alertDialog.setMessage("Are you sure you want to mark this Fellowship as done?");
        } else {
            alertDialog.setTitle("Retract mark");
            alertDialog.setMessage("Are you sure you want to un-mark this Fellowship as done?");
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                viewModel.markAsDoneMethod(markAsDone);
            } });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //...
            }});

        alertDialog.show();
    }

    private void receiptMethodForUser() {
        if (ownerOfFellowship) {
            choosePdf();
        } else {
            try {
                viewModel.downloadReceipt();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                    viewModel.setReceiptProperties();
                }
            });
        }
    }
}