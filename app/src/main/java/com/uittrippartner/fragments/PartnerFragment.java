package com.uittrippartner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uittrippartner.R;

public class PartnerFragment extends Fragment {

    public PartnerFragment() {
        // Required empty public constructor
    }

    public static PartnerFragment newInstance(String param1, String param2) {
        return new PartnerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner, container, false);
    }
}