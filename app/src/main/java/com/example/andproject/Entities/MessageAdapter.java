package com.example.andproject.Entities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.andproject.R;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Activity activity;
    private ArrayList<Message> messageList;
    private static LayoutInflater inflater = null;

    public MessageAdapter (Activity activity, int textViewResourceId,ArrayList<Message> messageList) {
        super(activity, textViewResourceId, messageList);
        try {
            this.activity = activity;
            this.messageList = messageList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return messageList.size();
    }

    public Message getItem(Message position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView messageUser;
        public TextView messageTime;
        public TextView messageText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.message, null);
                holder = new ViewHolder();

                holder.messageUser = (TextView) vi.findViewById(R.id.messageUser);
                holder.messageTime = (TextView) vi.findViewById(R.id.messageTime);
                holder.messageText = (TextView) vi.findViewById(R.id.messageText);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.messageUser.setText(messageList.get(position).senderName);
            holder.messageTime.setText(messageList.get(position).getTime());
            holder.messageText.setText(messageList.get(position).messageText);


        } catch (Exception e) {


        }
        return vi;
    }
}
