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

public class JoinedFellowshipItemAdapter extends ArrayAdapter<Fellowship> {
    private Activity activity;
    private ArrayList<Fellowship> fellowshipList;
    private static LayoutInflater inflater = null;

    public JoinedFellowshipItemAdapter(Activity activity, int textViewResourceId, ArrayList<Fellowship> fellowshipList) {
        super(activity, textViewResourceId, fellowshipList);
        try {
            this.activity = activity;
            this.fellowshipList = fellowshipList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return fellowshipList.size();
    }

    public Fellowship getItem(Fellowship position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView webShopItemText;
        public ImageView statusItemImage;
        public TextView amountItemText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.joined_fellowship_item, null);
                holder = new ViewHolder();

                holder.webShopItemText = (TextView) vi.findViewById(R.id.webShopItemText);
                holder.statusItemImage = (ImageView) vi.findViewById(R.id.statusItemImage);
                holder.amountItemText = (TextView) vi.findViewById(R.id.amountItemText);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.webShopItemText.setText(fellowshipList.get(position).getWebshop());
            if (fellowshipList.get(position).getIsCompleted() == 1) {
                holder.statusItemImage.setImageResource(R.drawable.checkmark);
            } else {
                holder.statusItemImage.setImageResource(R.drawable.cross);
            }
            holder.amountItemText.setText(fellowshipList.get(position).getAmountNeeded() + " DKK");

        } catch (Exception e) {


        }
        return vi;
    }
}
