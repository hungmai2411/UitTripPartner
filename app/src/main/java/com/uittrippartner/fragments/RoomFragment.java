package com.uittrippartner.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.uittrippartner.R;
import com.uittrippartner.activities.AddRoomActivity;
import com.uittrippartner.activities.MainPartnerActivity;
import com.uittrippartner.adapter.RoomAdapter;
import com.uittrippartner.hotel.room.Photo;
import com.uittrippartner.hotel.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {
    Toolbar toolbar;
    RecyclerView rcvRooms;
    RoomAdapter roomAdapter;
    List<Room> roomList;
    FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    Dialog dialog;

    public RoomFragment() {
        // Required empty public constructor
    }

    public static RoomFragment newInstance() {
        return new RoomFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels*0.90), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        toolbar = view.findViewById(R.id.toolbar);
        ((MainPartnerActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainPartnerActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvRooms = view.findViewById(R.id.rcvRooms);
        roomAdapter = new RoomAdapter(getActivity(), dialog, new RoomAdapter.IClickRoomListener() {
            @Override
            public void onCallBack(Room room) {
                Intent intent = new Intent(getContext(), AddRoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("room",room);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        roomList = new ArrayList<>();

        mFireStore.collection("Hotels/" + 1428 + "/rooms")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot document : value) {
                                    Room room = new Room();

                                    List<Photo> list = (List<Photo>) document.get("photos");
                                    List<Photo> listTmp = new ArrayList<>();

                                    for (int i = 0; i < list.size(); i++) {
                                        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                                        final Photo pojo = mapper.convertValue(list.get(i), Photo.class);
                                        listTmp.add(pojo);
                                    }

                                    room.setRoomArea(document.getString("roomArea"));
                                    room.setId(document.getId());
                                    room.setName(document.getString("name"));
                                    room.setCancelPolicies(document.getString("cancelPolicies"));
                                    room.setFacilities(document.getString("facilities"));

                                    if (document.get("number") != null)
                                        room.setNumber((Long) document.get("number"));

                                    room.setPhotos(listTmp);
                                    room.setPrice((Long) document.get("price"));

                                    if(!checkExist(document.getId())){
                                        roomList.add(room);
                                    }
                                }
                            }
                            roomAdapter.notifyDataSetChanged();

                        }
                    }
                });


        roomAdapter.setData(roomList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvRooms.setLayoutManager(linearLayoutManager);
        rcvRooms.setAdapter(roomAdapter);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_room, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.ic_add) {
            startActivity(new Intent(getContext(), AddRoomActivity.class));
        }

        return true;
    }

    private boolean checkExist(String id){
        for(Room room : roomList){
            if(room.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
}
