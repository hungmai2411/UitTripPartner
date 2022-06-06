package com.uittrippartner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.R;
import com.uittrippartner.activities.BookingDetailActivity;
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

    public BookingPartnerFragment() {
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
}