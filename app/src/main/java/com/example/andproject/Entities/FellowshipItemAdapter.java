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

            holder.webShopItemText.setText(fellowshipList.get(position).first.webshop);
            holder.categoryItemText.setText(fellowshipList.get(position).first.category);
            holder.paymentMethodItemText.setText(fellowshipList.get(position).first.paymentMethod);
            holder.daysLeftItemText.setText(calculateDaysLeft(fellowshipList.get(position).first.deadline));
            holder.distanceItemText.setText(distanceBetween(fellowshipList.get(position).second, fellowshipList.get(position).first.pickupCoordinates));
            holder.amountItemText.setText(fellowshipList.get(position).first.amountNeeded + " DKK");

        } catch (Exception e) {


        }
        return vi;
    }

    private String calculateDaysLeft(String deadline) throws ParseException {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date(); // Today
        cal1.setTime(date);
        date = sdf.parse(deadline);
        cal2.setTime(date);

        return "" + daysBetween(cal1.getTime(),cal2.getTime());
    }

    private int daysBetween(Date d1, Date d2){
        System.out.println("læs: days: " + (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)));
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String distanceBetween(String usersLocation, String pickupLocation) {
        // We convert the strings into doubles
        double lat1, lng1, lat2, lng2;

        String[] parts = usersLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat1 = Double.parseDouble(parts[0]);
        lng1 = Double.parseDouble(parts[1]);

        parts = pickupLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat2 = Double.parseDouble(parts[0]);
        lng2 = Double.parseDouble(parts[1]);

        //returns distance in meters
            double a = (lat1 - lat2) * distPerLat(lat1);
            double b = (lng1 - lng2) * distPerLng(lng1);

            return String.format("%.2f", (Math.sqrt(a * a + b * b))) + " meters";
    }

    private static double distPerLng(double lng){
        return 0.0003121092*Math.pow(lng, 4)
                +0.0101182384*Math.pow(lng, 3)
                -17.2385140059*lng*lng
                +5.5485277537*lng+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
}
