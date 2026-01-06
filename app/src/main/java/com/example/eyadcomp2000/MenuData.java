package com.example.eyadcomp2000;

import java.util.ArrayList;
import java.util.List;

public class MenuData {
    public static List<String> soupList = new ArrayList<>();
    public static List<String> specialsList = new ArrayList<>();
    public static List<String> appetizersList = new ArrayList<>();
    public static List<String> mainCourseList = new ArrayList<>();
    public static List<String> dessertsList = new ArrayList<>();
    public static List<String> drinksList = new ArrayList<>();

    public static List<String> reservationsList = new ArrayList<>();
    public static List<String> ongoingReservationsList = new ArrayList<>();
    public static List<String> customerComplaints = new ArrayList<>();

    // NEW: Shared counter for the staff notification bell
    public static int notificationCount = 0;

    static {
        soupList.add("Tomato Soup - $5.00");
        specialsList.add("Chef's Special Steak - $25.00");
        appetizersList.add("Garlic Bread - $4.50");
        mainCourseList.add("Chicken Dish - $15.00");
        dessertsList.add("Chocolate Cake - $8.00");
        drinksList.add("Fresh Orange Juice - $4.00");
    }

    public static void addReservation(String details) {
        if (details != null && !details.isEmpty()) {
            reservationsList.add(details);
            notificationCount++; // Trigger notification for new reservation
        }
    }

    public static void addComplaint(String comment) {
        if (comment != null && !comment.isEmpty()) {
            customerComplaints.add(comment);
            notificationCount++; // Trigger notification for new complaint
        }
    }

    public static void deleteReservation(int position) {
        if (position >= 0 && position < reservationsList.size()) {
            reservationsList.remove(position);
        }
    }
}