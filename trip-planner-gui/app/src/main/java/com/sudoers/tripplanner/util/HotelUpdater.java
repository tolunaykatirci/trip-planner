package com.sudoers.tripplanner.util;

import android.os.AsyncTask;

import com.sudoers.tripplanner.TripPlannerApplication;

import java.util.ArrayList;

public class HotelUpdater extends AsyncTask<Void, Void, ArrayList<String>> {

    private TripPlannerApplication application;

    // background operation for updating active hotels

    public HotelUpdater(TripPlannerApplication application) {
        this.application = application;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        TripPlannerService tp = new TripPlannerService();
        return (ArrayList<String>) tp.getAllHotels();
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if(strings != null)
            application.updateHotelsInner(strings);
    }
}
