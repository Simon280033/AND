package com.example.andproject.Entities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andproject.R;

import java.util.ArrayList;

// This class is an itemadapter for Listviews. It ensures that the profile comment is displayed in the correct way
public class ProfileCommentItemAdapter extends ArrayAdapter<ProfileComment> {
    private Activity activity;
    private ArrayList<ProfileComment> messageList;
    private static LayoutInflater inflater = null;

    public ProfileCommentItemAdapter(Activity activity, int textViewResourceId, ArrayList<ProfileComment> messageList) {
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

    public ProfileComment getItem(ProfileComment position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView messageUser;
        public TextView messageTime;
        public TextView messageText;
        public ImageView messageAvatarView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.profile_comment_item, null);
                holder = new ViewHolder();

                holder.messageUser = (TextView) vi.findViewById(R.id.messageUser);
                holder.messageTime = (TextView) vi.findViewById(R.id.messageTime);
                holder.messageText = (TextView) vi.findViewById(R.id.messageText);
                holder.messageAvatarView = (ImageView) vi.findViewById(R.id.messageAvatarView);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.messageUser.setText(messageList.get(position).getSenderName());
            holder.messageTime.setText(messageList.get(position).getTime());
            holder.messageText.setText(messageList.get(position).getMessageText());
            // We use glide to set the image
            Glide.with(holder.messageAvatarView).load(Uri.parse(messageList.get(position).getSenderImageUrl())).apply(RequestOptions.circleCropTransform()).into(holder.messageAvatarView);

        } catch (Exception e) {


        }
        return vi;
    }
}
