package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FirstActivity;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevelopersFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ExpenseList_Debug";

    private Button deleteLocalDataButton;
    private Button deleteRemoteDataButton;
    private AppData appData;
    private Context context;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        prefs = getActivity().getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_developers, container, false);

        deleteLocalDataButton = (Button) layout.findViewById(R.id.deleteLocalDataButton);
        deleteLocalDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                //Reset AppData contents
                appData.clearAppData(context);

                //Start FirstActivity
                Intent intent = new Intent(context, FirstActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        deleteRemoteDataButton = (Button) layout.findViewById(R.id.deleteRemoteDataButton);
        deleteRemoteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Feature to be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        return layout;
    }


}
