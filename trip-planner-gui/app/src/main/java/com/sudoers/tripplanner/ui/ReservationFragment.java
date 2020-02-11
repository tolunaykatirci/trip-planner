package com.sudoers.tripplanner.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.sudoers.tripplanner.R;
import com.sudoers.tripplanner.TripPlannerApplication;
import com.sudoers.tripplanner.util.TripPlannerService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ReservationFragment extends Fragment {

    private final Calendar calendar = Calendar.getInstance();
    private TripPlannerApplication tripPlannerApplication;

    // start date variables
    private EditText startDate;
    private long startDateEpoch;
    DatePickerDialog.OnDateSetListener startDatePicker;

    // end date variables
    private EditText endDate;
    private long endDateEpoch;
    DatePickerDialog.OnDateSetListener endDatePicker;

    // declare spinner variables
    private Spinner hotelSpinner;
    private Spinner airlineSpinner;

    // declare TextView variables
    private TextView registrationName;
    private TextView travelerCount;

    // preferred hotel/airline
    private String preferredHotel;
    private String preferredAirline;

    // declare buttons
    private Button makeReservationBtn;
    private Button clearAllBtn;

    // selected hotel/airline
    private String selectedHotel;
    private String selectedAirline;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reservation, container, false);


        // start date picker
        startDate = (EditText) root.findViewById(R.id.startDate);
        startDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateEpoch = calendar.getTimeInMillis();

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                startDate.setText(sdf.format(calendar.getTime()));
                if(startDateEpoch > endDateEpoch){
                    endDateEpoch = startDateEpoch;
                    endDate.setText(sdf.format(calendar.getTime()));
                }

            }

        };
        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), startDatePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // end date picker
        endDate = (EditText) root.findViewById(R.id.endDate);
        endDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateEpoch = calendar.getTimeInMillis();

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                endDate.setText(sdf.format(calendar.getTime()));
                if(endDateEpoch < startDateEpoch){
                    startDateEpoch = endDateEpoch;
                    startDate.setText(sdf.format(calendar.getTime()));
                }
            }

        };
        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), endDatePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // declare TextViews
        registrationName = (TextView) root.findViewById(R.id.passengerName);
        travelerCount = (TextView) root.findViewById(R.id.passengerCount);

        tripPlannerApplication = (TripPlannerApplication) this.getActivity().getApplication();
//        tripPlannerApplication.updateHotels();
//        tripPlannerApplication.updateAirlines();

        // define hotel spinner
        hotelSpinner = (Spinner) root.findViewById(R.id.hotelSpinner);
        hotelSpinner.setAdapter(tripPlannerApplication.hotelAdapter);
        hotelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferredHotel = tripPlannerApplication.activeHotels.get(position);
                System.out.println(preferredHotel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                preferredHotel = null;
            }
        });

        // define hotel spinner
        airlineSpinner = (Spinner) root.findViewById(R.id.airlineSpinner);
        airlineSpinner.setAdapter(tripPlannerApplication.airlineAdapter);
        airlineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferredAirline = tripPlannerApplication.activeAirlines.get(position);
                System.out.println(preferredAirline);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                preferredAirline = null;
            }
        });

        // buttons
        makeReservationBtn = (Button) root.findViewById(R.id.makeReservationButton);
        makeReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeReservation();
            }
        });

        clearAllBtn = (Button) root.findViewById(R.id.clearAllButton);
        clearAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });


        return root;
    }


    // make reservation
    private void makeReservation(){
        // if any value is empty, show warning
        if(startDate == null || startDate.getText().toString().equals("")
                || endDate == null || endDate.getText().toString().equals("")
                || registrationName == null || registrationName.getText().toString().equals("")
                || travelerCount == null || travelerCount.getText().toString().equals("")
                || preferredHotel == null || preferredHotel.equals("")
                || preferredAirline == null || preferredAirline.equals("")){
            Toast.makeText(getContext(), "Please fill all sections!", Toast.LENGTH_LONG).show();
        } else {
            // else make reservation
            final TripPlannerService tp = new TripPlannerService();
            final List<String> availableHotels = tp.getAvailableHotels(startDate.getText().toString(), endDate.getText().toString(), Integer.parseInt(travelerCount.getText().toString()));
            final List<String> availableAirlines = tp.getAvailableAirlines(startDate.getText().toString(), endDate.getText().toString(), Integer.parseInt(travelerCount.getText().toString()));

            if(availableHotels.size() <= 0){
                // there is no available hotel
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Error");
                builder.setMessage("Unfortunately, there is no available hotel!");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if(availableAirlines.size() <= 0){
                // there is no available airline
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Error");
                builder.setMessage("Unfortunately, there is no available airline!");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                // there are available hotels / airlines
                selectedHotel = preferredHotel;
                selectedAirline = preferredAirline;

                if(!availableHotels.contains(preferredHotel)){
                    // preferred hotel is unavailable, select new hotel from dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Preferred hotel is unavailable. Select another hotel");

                    builder.setItems(availableHotels.toArray(new String[availableHotels.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            selectedHotel = availableHotels.get(which);
                            selectAirline(availableAirlines);
                        }

                    });
                    builder.show();
                } else {
                    selectAirline(availableAirlines);
                }

            }

        }
    }


    private void selectAirline(final List<String> availableAirlines) {
        if(!availableAirlines.contains(preferredAirline)){
            // preferred airline is unavailable, select new airline from dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Preferred airline is unavailable. Select another airline");
            builder.setItems(availableAirlines.toArray(new String[availableAirlines.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    selectedAirline = availableAirlines.get(which);
                    completeRegistration();
                }
            });
            builder.show();
        } else {
            completeRegistration();
        }

    }

    private void completeRegistration() {
        // create string with parameters
        StringBuilder sb = new StringBuilder();
        sb.append("Start Date: ").append(startDate.getText().toString()).append("\n")
                .append("End Date: ").append(endDate.getText().toString()).append("\n")
                .append("Registration Name: ").append(registrationName.getText()).append("\n")
                .append("Traveler Count: ").append(travelerCount.getText()).append("\n")
                .append("Selected Hotel: ").append(selectedHotel).append("\n")
                .append("Selected Airline: ").append(selectedAirline).append("\n");

        final TripPlannerService tp = new TripPlannerService();

        // create alert dialog to complete reservation
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Complete Reservation");
        builder.setMessage(sb.toString());
        builder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // make reservation
                boolean result = tp.makeReservation(startDate.getText().toString(),
                        endDate.getText().toString(),
                        registrationName.getText().toString(),
                        travelerCount.getText().toString(),
                        selectedHotel,
                        selectedAirline);
                if(result)
                    Toast.makeText(getContext(), "Reservation successfully created", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getContext(), "Reservation couldn't created", Toast.LENGTH_LONG).show();
                }
                clearAll();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // clear EditTexts
    private void clearAll(){
        startDate.setText("");
        endDate.setText("");
        registrationName.setText("");
        travelerCount.setText("");
    }

}
