package com.example.andproject.Entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DayDifferenceCalculator {

    public static int calculateDaysLeft(String deadline) throws ParseException {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date(); // Today
        cal1.setTime(date);
        date = sdf.parse(deadline);
        cal2.setTime(date);

        return daysBetween(cal1.getTime(),cal2.getTime());
    }

    private static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)); // Millis -> sekunder -> minutter -> døgn
    }

}
