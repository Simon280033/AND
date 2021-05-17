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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        setContentView(R.layout.activity_chat);

        findViews();

        setButtonActions();

        bindUiElements();

        viewModel.displayChatMessages();
    }

    private void bindUiElements() {
        // We bind the spinner for our own Fellowships
        final Observer<ArrayList<Message>> ownFellowshipsObserver = new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Message> newValue) {
                System.out.println("læs: messagelist changed");
                //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                MessageAdapter madb = new MessageAdapter(ChatActivity.this, 0, newValue);

                messageList.setAdapter(madb);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getMessagesList().observe(this, ownFellowshipsObserver);
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

    private void sendMessage() {
        if (messageInput.length() == 0) {
            return;
        }

        viewModel.sendMessage(messageInput.getText().toString());

        // Clear the input
        TextKeyListener.clear(messageInput.getText());
    }
}