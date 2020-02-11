package com.sudoers.tripplanner;

import android.app.Application;
import android.widget.ArrayAdapter;

import com.sudoers.tripplanner.util.AirlineUpdater;
import com.sudoers.tripplanner.util.HotelUpdater;

import java.util.ArrayList;


public class TripPlannerApplication extends Application {

    public ArrayList<String> activeHotels = new ArrayList<>();
    public ArrayAdapter<String> hotelAdapter;

    public ArrayList<String> activeAirlines = new ArrayList<>();
    public ArrayAdapter<String> airlineAdapter;


    @Override
    public void onCreate() {
        super.onCreate();

        // create array adapters
        hotelAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, activeHotels);
        airlineAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, activeAirlines);

        // updates hotels in every 30 secs
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    updateHotels();
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // updates airlines in every 30 secs
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    updateAirlines();
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void updateHotels(){
        new HotelUpdater(this).execute();

    }

    public void updateAirlines(){
        new AirlineUpdater(this).execute();
    }

    // update hotel adapter
    public void updateHotelsInner(ArrayList<String> activeHotels) {
        hotelAdapter.clear();
        hotelAdapter.addAll(activeHotels);
        hotelAdapter.notifyDataSetChanged();
        System.out.println("Hotels Updated!");
    }

    // update airline adapter
    public void updateAirlinesInner(ArrayList<String> activeAirlines) {
        airlineAdapter.clear();
        airlineAdapter.addAll(activeAirlines);
        airlineAdapter.notifyDataSetChanged();
        System.out.println("Airlines Updated!");
    }


}
