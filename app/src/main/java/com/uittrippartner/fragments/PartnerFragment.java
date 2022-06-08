package com.uittrippartner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.R;
import com.uittrippartner.adapter.ParticipantAdapter;
import com.uittrippartner.hotel.Participant;

import java.util.ArrayList;
import java.util.List;

public class PartnerFragment extends Fragment {

    RecyclerView rcvPartners;
    ParticipantAdapter participantAdapter;
    List<Participant> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PartnerFragment() {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvPartners = view.findViewById(R.id.rcvPartners);
        participantAdapter = new ParticipantAdapter(getContext());
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvPartners.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvPartners.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        rcvPartners.addItemDecoration(dividerItemDecoration);

        participantAdapter.addList(list);
        rcvPartners.setAdapter(participantAdapter);

        db.collection("partners").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Participant participant = new Participant();
                        if (document.getString("role").equals("partner")) {
                            participant.setName(document.getString("name"));
                            participant.setUrlImage(document.getString("image"));
                            list.add(participant);
                        }
                    }
                    participantAdapter.notifyDataSetChanged();
                } else {
                }
            }
        });
    }
}
