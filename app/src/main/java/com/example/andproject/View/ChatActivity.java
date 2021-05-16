package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.MessageAdapter;
import com.example.andproject.R;
import com.example.andproject.ViewModel.ChatViewModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel viewModel;

    private FloatingActionButton sendButton;
    private EditText messageInput;
    private ListView messageList;

    private FirebaseListAdapter<Message> adapter;

    private MutableLiveData<ArrayList<Message>> yourFellowshipsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        setContentView(R.layout.activity_chat);

        findViews();

        setButtonActions();

       // setUi();

       // displayChatMessages();

        yourFellowshipsList = new MutableLiveData<ArrayList<Message>>();
        // We bind the spinner for our own Fellowships
        final Observer<ArrayList<Message>> ownFellowshipsObserver = new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Message> newValue) {
System.out.println("læs: onchanged");
                //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                MessageAdapter madb = new MessageAdapter(ChatActivity.this, 0, newValue);


                messageList.setAdapter(madb);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        yourFellowshipsList.observe(this, ownFellowshipsObserver);

        displayChatMessages();

    }

    private void setUi() {

    }

    private void findViews() {
        sendButton = findViewById(R.id.sendButton);
        messageInput = findViewById(R.id.messageInput);
        messageList = findViewById(R.id.messageList);
    }

    private void setButtonActions() {
        sendButton.setOnClickListener((View v) -> {
            sendMessage();
        });
    }

    private void displayChatMessages() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("messages").orderByChild(viewModel.getViewFellowshipInfo().id);

        ownerQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ArrayList<Message> ms = new ArrayList<>();
                System.out.println("læs: child added");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("fellowshipId");
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
                    yourFellowshipsList.setValue(ms);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ArrayList<Message> ms = new ArrayList<>();

                System.out.println("læs: child changed");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("fellowshipId");;
                        String senderId = ((HashMap<String, String>) issue.getValue()).get("senderId");;
                        String senderName = ((HashMap<String, String>) issue.getValue()).get("senderName");;
                        String senderImageUrl = ((HashMap<String, String>) issue.getValue()).get("senderImageUrl");;
                        String receiverId = ((HashMap<String, String>) issue.getValue()).get("receiverId");;
                        String receiverName = ((HashMap<String, String>) issue.getValue()).get("receiverName");;
                        String messageText = ((HashMap<String, String>) issue.getValue()).get("messageText");;
                        Long messageTime = ((HashMap<String, Long>) issue.getValue()).get("messageTime");;

                        //String messageDateTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messageTime).toString();

                        Message message = new Message(fellowshipId, senderId, senderName, senderImageUrl, receiverId, receiverName, messageText);

                        message.setMessageTime(messageTime);

                        ms.add(message);

                    }
                    yourFellowshipsList.setValue(ms);

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

    private void sendMessage() {
        if (messageInput.length() == 0) {
            return;
        }

        String fellowshipId = viewModel.getViewFellowshipInfo().id;
        String senderId = viewModel.getCurrentUser().getValue().getUid();
        String senderName = viewModel.getThisUser().displayName; // We get this from here, as the user could have changed it from the autheticator
        String senderImageUrl = viewModel.getThisUser().imageUrl;
        String receiverId = viewModel.getChatReceiver().id;
        String receiverName = viewModel.getChatReceiver().displayName;
        String messageText = messageInput.getText().toString();

        Message message = new Message(fellowshipId, senderId, senderName, senderImageUrl, receiverId, receiverName, messageText);

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("messages").child(fellowshipId)
                .push()
                .setValue(message);

        // Clear the input
        TextKeyListener.clear(messageInput.getText());
    }
}