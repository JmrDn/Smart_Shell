package com.example.smartshell.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarUtils {
    public static LocalDate startSelectedDate;
    public static LocalDate endSelectedDate;


    public static String monthYearFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }


    public static String monthDayYearFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return date.format(formatter);
    }

    public static String monthDayYearFromDateForWeek(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM  yyyy");
        return date.format(formatter);
    }




    public static String yearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }




    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();

        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }

        return  days;
    }


    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }

}