package com.uittrippartner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uittrippartner.R;
import com.uittrippartner.adapter.ViewPageAdapter;

public class ParticipantFragment extends Fragment {

    ViewPager2 viewPager2;
    ViewPageAdapter viewPageAdapter;
    TabLayout tabLayout;

    public ParticipantFragment() {
        // Required empty public constructor
    }

    public static ParticipantFragment newInstance(String param1, String param2) {
        return new ParticipantFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_participant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager2);
        viewPageAdapter = new ViewPageAdapter(getActivity());
        viewPager2.setAdapter(viewPageAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Partners");
                    break;
                case 1:
                    tab.setText("Users");
                    break;
            }
        }).attach();
    }
}