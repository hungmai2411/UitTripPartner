package com.uittrippartner.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.uittrippartner.R;
import com.uittrippartner.activities.EditPartnerProfileActivity;

public class PartnerProfileFragment extends Fragment {
    FirebaseFirestore firestore;
    String partnerID;
    TextView txtCompanyName, txtCompanyEmail, txtName, txtEmail, txtPhoneNumber, txtAddress;
    ImageView imgProfile;
    ImageButton btnFacebook, btnGmail, btnWeb;
    String fbLink, website;

    public PartnerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();

        setPartnerInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_partner_profile, container, false);
        final ImageButton buttonMore = v.findViewById(R.id.btnMore);
        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), buttonMore);
                popupMenu.getMenuInflater().inflate(R.menu.profile_context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.itemEditProfile:
                                startActivity(new Intent(getContext(), EditPartnerProfileActivity.class));
                                break;
                            case R.id.itemLogout:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtCompanyName = view.findViewById(R.id.txtCompanyName);
        txtCompanyEmail = view.findViewById(R.id.txtCompanyEmail);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        txtAddress = view.findViewById(R.id.txtLocation);
        imgProfile = view.findViewById(R.id.imgProfile);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnGmail = view.findViewById(R.id.btnGmail);
        btnWeb = view.findViewById(R.id.btnWeb);

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(fbLink));
                startActivity(intent);
            }
        });

        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(website));
                startActivity(intent);
            }
        });
    }

    public void setPartnerInformation(){
        FirebaseUser partner = FirebaseAuth.getInstance().getCurrentUser();
        if(partner != null){
            partnerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        firestore.collection("partners").document(partnerID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String email = task.getResult().getString("email");
                        String phoneNumber = task.getResult().getString("phonenumber");
                        String address = task.getResult().getString("address");
                        String avatar = task.getResult().getString("avatar");
                        fbLink = task.getResult().getString("facebook");
                        website = task.getResult().getString("website");

                        txtCompanyName.setText(name);
                        txtCompanyEmail.setText(email);
                        txtName.setText(name);
                        txtEmail.setText(email);
                        txtPhoneNumber.setText(phoneNumber);
                        txtAddress.setText(address);
                        Glide.with(PartnerProfileFragment.this).load(avatar).error(R.drawable.company_profile).into(imgProfile);
                    }
                }
            }
        });
    }
}