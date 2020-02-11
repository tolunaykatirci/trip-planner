package com.sudoers.tripplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sudoers.tripplanner.R;
import com.sudoers.tripplanner.TripPlannerApplication;


public class HotelFragment extends Fragment{

    ListView hotelListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hotel, container, false);

        // define listView
        hotelListView = (ListView) root.findViewById(R.id.hotelList);
        hotelListView.setAdapter(((TripPlannerApplication) this.getActivity().getApplication()).hotelAdapter);

        return root;
    }


}
