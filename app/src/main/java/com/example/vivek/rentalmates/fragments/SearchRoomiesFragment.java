package com.example.vivek.rentalmates.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRoomiesFragment extends android.support.v4.app.Fragment {


    public SearchRoomiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_roomies, container, false);
    }


}
