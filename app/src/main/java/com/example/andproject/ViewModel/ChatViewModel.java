package com.example.andproject.ViewModel;

import android.app.Application;
import android.text.method.TextKeyListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatViewModel extends AndroidViewModel {
    private final Model model;

    private MutableLiveData<ArrayList<Message>> messagesList;

    public ChatViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public MutableLiveData<ArrayList<Message>> getMessagesList() {
        if (messagesList == null) {
            messagesList = new MutableLiveData<ArrayList<Message>>();
        }
        return messagesList;
    }

    public void sendMessage(String messageText) {
        String fellowshipId = model.getViewFellowshipInfo().id;

        String senderId = model.getCurrentUserData().getValue().getUid();

        String senderName = model.getThisUser().displayName; // We get this from here, as the user could have changed it from the autheticator

        String senderImageUrl = model.getThisUser().imageUrl;

        String receiverId = model.getChatReceiver().id;

        String receiverName = model.getChatReceiver().displayName;


        Message message = new Message(fellowshipId, senderId, senderName, senderImageUrl, receiverId, receiverName, messageText);

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("messages").child(fellowshipId)
                .push()
                .setValue(message);
    }

    public void displayChatMessages() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("messages").orderByChild(model.getViewFellowshipInfo().id);

        ownerQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ArrayList<Message> ms = new ArrayList<>();
                System.out.println("læs: child added");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("fellowshipId");
                        if (fellowshipId.equals(model.getViewFellowshipInfo().id)) {
                            String senderId = ((HashMap<String, String>) issue.getValue()).get("senderId");
                            String senderName = ((HashMap<String, String>) issue.getValue()).get("senderName");
                            String senderImageUrl = ((HashMap<String, String>) issue.getValue()).get("senderImageUrl");;
                            String receiverId = ((HashMap<String, String>) issue.getValue()).get("receiverId");
                            String receiverName = ((HashMap<String, String>) issue.getValue()).get("receiverName");
                            String messageText = ((HashMap<String, String>) issue.getValue()).get("messageText");
                            Long messageTime = ((HashMap<String, Long>) issue.getValue()).get("messageTime");

                            //String messageDateTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messageTime).toString();

                            Message message = new Message(fellowshipId, senderId, senderName, senderImageUrl, receiverId, receiverName, messageText);

                            System.out.println("læs: " + message.messageText);

                            ms.add(message);
                        }
                    }
                    if (ms.size() > 0) {
                        messagesList.setValue(ms);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ArrayList<Message> ms = new ArrayList<>();

                System.out.println("læs: child changed");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("fellowshipId");
                        if (fellowshipId.equals(model.getViewFellowshipInfo().id)) {
                            String senderId = ((HashMap<String, String>) issue.getValue()).get("senderId");
                            String senderName = ((HashMap<String, String>) issue.getValue()).get("senderName");
                            String senderImageUrl = ((HashMap<String, String>) issue.getValue()).get("senderImageUrl");;
                            String receiverId = ((HashMap<String, String>) issue.getValue()).get("receiverId");
                            String receiverName = ((HashMap<String, String>) issue.getValue()).get("receiverName");
                            String messageText = ((HashMap<String, String>) issue.getValue()).get("messageText");
                            Long messageTime = ((HashMap<String, Long>) issue.getValue()).get("messageTime");

                            //String messageDateTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messageTime).toString();

                            Message message = new Message(fellowshipId, senderId, senderName, senderImageUrl, receiverId, receiverName, messageText);

                            System.out.println("læs: " + message.messageText);

                            ms.add(message);
                        }
                    }
                    if (ms.size() > 0) {
                        messagesList.setValue(ms);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                System.out.println("læs: child Removed");

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("læs: child Moved");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("læs: child onCancelled");

            }
        });
    }

    public User getThisUser() {
        return model.getThisUser();
    }

    public User getChatReceiver() {
        return model.getChatReceiver();
    }

    public Fellowship getViewFellowshipInfo() {
        return model.getViewFellowshipInfo();
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return model.getCurrentUserData();
    }

}
