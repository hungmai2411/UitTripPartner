package com.uittrippartner.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.uittrippartner.R;
import com.uittrippartner.activities.BookingDetailActivity;
import com.uittrippartner.activities.MainPartnerActivity;
import com.uittrippartner.adapter.BookingPartnerAdapter;
import com.uittrippartner.hotel.Booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingPartnerFragment extends Fragment {

    RecyclerView rcvBookings;
    BookingPartnerAdapter bookingPartnerAdapter;
    List<Booking> bookingList = new ArrayList<>();
    FirebaseFirestore db;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Successfull", Toast.LENGTH_LONG).show();
                    showDialog(getContext());
                    db.collection("Hotels/" + 1428 + "/booked")
                            .document(result.getContents())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Booking booking = document.toObject(Booking.class);
                                Timestamp timestamp = (Timestamp) document.get("timestamp");
                                Date date = timestamp.toDate();
                                booking.setDate(date);
                                booking.setIdBooking(document.getId());
                                dismissDialog();
                                Intent intent = new Intent(getContext(), BookingDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("booking", booking);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });

    public BookingPartnerFragment() {
    }

    public void showDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialog() {
        progressDialog.dismiss();
    }

    public static BookingPartnerFragment newInstance() {
        return new BookingPartnerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_partner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((MainPartnerActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainPartnerActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvBookings = view.findViewById(R.id.rcvBookings);

        bookingPartnerAdapter = new BookingPartnerAdapter(getContext(), new BookingPartnerAdapter.IClickBookingListener() {
            @Override
            public void onCallBack(Booking booking) {
                Intent intent = new Intent(getContext(), BookingDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("booking", booking);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        bookingList = new ArrayList<>();

        db.collection("Hotels/" + 1428 + "/booked")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    QueryDocumentSnapshot document = dc.getDocument();

                                    Booking booking = document.toObject(Booking.class);
                                    Timestamp timestamp = (Timestamp) document.get("timestamp");
                                    Date date = timestamp.toDate();
                                    booking.setDate(date);
                                    booking.setIdBooking(document.getId());

                                    switch (dc.getType()) {
                                        case ADDED:
                                            bookingList.add(booking);
                                            break;
                                        case MODIFIED:
                                            removeBooking(document.getId());
                                            bookingList.add(booking);
                                            break;
                                        case REMOVED:
                                            Log.d("tag", document.getId());
                                            removeBooking(document.getId());
                                            break;
                                    }
                                }
                            }
                            bookingPartnerAdapter.notifyDataSetChanged();
                        }
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvBookings.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvBookings.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        rcvBookings.addItemDecoration(dividerItemDecoration);

        bookingPartnerAdapter.addData(bookingList);
        rcvBookings.setAdapter(bookingPartnerAdapter);
    }

    private void removeBooking(String id) {
        List<Booking> list = new ArrayList<>();

        for (Booking booking : bookingList) {
            if (booking.getIdBooking().equals(id)) {
                list.add(booking);
            }
        }

        bookingList.removeAll(list);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_booking, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.icScan) {
            barcodeLauncher.launch(new ScanOptions());
        }

        return true;
    }
}