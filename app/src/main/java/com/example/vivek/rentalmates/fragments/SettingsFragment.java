package com.example.vivek.rentalmates.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment_Debug";

    private Button accountbutton;
    private Button cleardatabutton;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        accountbutton = (Button) getView().findViewById(R.id.accountbutton);
        cleardatabutton = (Button) getView().findViewById(R.id.cleardatabutton);
        accountbutton.setOnClickListener(this);
        cleardatabutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.accountbutton:
                Log.d(TAG, "account button clicked");
                Intent intent = new Intent(getActivity(), MyLoginActivity.class);
                startActivity(intent);
                break;

            case R.id.cleardatabutton:
                SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                String path = getActivity().getApplicationContext().getFilesDir().getPath()+"/" + "expenses.tmp";
                File file = new File(path);
                file.delete();
                Toast.makeText(getActivity(), "SharedPreferences data cleared", Toast.LENGTH_LONG).show();
                Log.d(TAG, "SharedPreferences data cleared");
                break;

            default:
                Log.d(TAG, "Unknown button clicked");
                break;
        }
    }
}
