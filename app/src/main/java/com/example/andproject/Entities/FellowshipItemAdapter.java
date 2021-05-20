package com.example.andproject.Entities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.andproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// This class is an itemadapter for Listviews. It ensures that the FellowShip is displayed in the correct way
public class FellowshipItemAdapter extends ArrayAdapter<Pair<Fellowship, String>> {
    private Activity activity;
    private ArrayList<Pair<Fellowship, String>> fellowshipList;
    private static LayoutInflater inflater = null;

    public FellowshipItemAdapter(Activity activity, int textViewResourceId, ArrayList<Pair<Fellowship, String>> fellowshipList) {
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
        public TextView categoryItemText;
        public TextView paymentMethodItemText;
        public TextView daysLeftItemText;
        public TextView distanceItemText;
        public TextView amountItemText;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.fellowship_item, null);
                holder = new ViewHolder();

                holder.webShopItemText = (TextView) vi.findViewById(R.id.webShopItemText);
                holder.categoryItemText = (TextView) vi.findViewById(R.id.categoryItemText);
                holder.paymentMethodItemText = (TextView) vi.findViewById(R.id.paymentMethodItemText);
                holder.daysLeftItemText = (TextView) vi.findViewById(R.id.daysLeftItemText);
                holder.distanceItemText = (TextView) vi.findViewById(R.id.distanceItemText);
                holder.amountItemText = (TextView) vi.findViewById(R.id.amountItemText);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            System.out.println("PLEASE" + fellowshipList.get(position).second + "please");
            holder.webShopItemText.setText(fellowshipList.get(position).first.getWebshop());
            holder.categoryItemText.setText(fellowshipList.get(position).first.getCategory());
            holder.paymentMethodItemText.setText(fellowshipList.get(position).first.getPaymentMethod());
            holder.daysLeftItemText.setText(DayDifferenceCalculator.calculateDaysLeft(fellowshipList.get(position).first.getDeadline()));
            holder.distanceItemText.setText(DistanceCalculator.distanceBetween(fellowshipList.get(position).second, fellowshipList.get(position).first.getPickupCoordinates()));
            holder.amountItemText.setText(fellowshipList.get(position).first.getAmountNeeded() + " DKK");

        } catch (Exception e) {


        }
        return vi;
    }
}