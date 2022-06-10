package com.uittrippartner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.R;
import com.uittrippartner.activities.AddBannerActivity;
import com.uittrippartner.activities.AddVoucherActivity;
import com.uittrippartner.activities.MainAdminActivity;
import com.uittrippartner.adapter.BannerAdapter;
import com.uittrippartner.adapter.VoucherAdapter;
import com.uittrippartner.hotel.Banner;
import com.uittrippartner.hotel.Voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BannerFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView rcvBanners;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BannerFragment() {
        // Required empty public constructor
    }

    public static BannerFragment newInstance(String param1, String param2) {
        return new BannerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((MainAdminActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainAdminActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvBanners = view.findViewById(R.id.rcvBanners);
        bannerAdapter = new BannerAdapter(getContext());
        bannerList = new ArrayList<>();

        db.collection("banners").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    Banner banner = new Banner();
                    banner.setImage(doc.getString("image"));
                    bannerList.add(banner);
                }
                bannerAdapter.notifyDataSetChanged();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvBanners.setLayoutManager(linearLayoutManager);
        bannerAdapter.setList(bannerList);
        rcvBanners.setAdapter(bannerAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_voucher, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.ic_add) {
            startActivity(new Intent(getContext(), AddBannerActivity.class));
        }

        return true;
    }
}