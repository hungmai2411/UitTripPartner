package com.uittrippartner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.uittrippartner.activities.AddRoomActivity;
import com.uittrippartner.activities.AddVoucherActivity;
import com.uittrippartner.activities.MainAdminActivity;
import com.uittrippartner.activities.MainPartnerActivity;
import com.uittrippartner.adapter.VoucherAdapter;
import com.uittrippartner.hotel.Voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VoucherFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView rcvVouchers;
    VoucherAdapter voucherAdapter;
    List<Voucher> voucherList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public VoucherFragment() {
        // Required empty public constructor
    }

    public static VoucherFragment newInstance(String param1, String param2) {
        return new VoucherFragment();
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
        return inflater.inflate(R.layout.fragment_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((MainAdminActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainAdminActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvVouchers = view.findViewById(R.id.rcvVouchers);
        voucherAdapter = new VoucherAdapter(getContext());
        voucherList = new ArrayList<>();

        db.collection("vouchers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    Voucher voucher = new Voucher();

                    try {
                        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(doc.getString("endDate"));
                        voucher.setEndDate(date1);
                        voucher.setCode(doc.getString("code"));
                        voucher.setDescription(doc.getString("description"));
                        voucher.setNumber(doc.getLong("number"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    voucherList.add(voucher);
                }
                voucherAdapter.notifyDataSetChanged();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvVouchers.setLayoutManager(linearLayoutManager);
        voucherAdapter.addList(voucherList);
        rcvVouchers.setAdapter(voucherAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_voucher, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.ic_add) {
            startActivity(new Intent(getContext(), AddVoucherActivity.class));
        }

        return true;
    }
}