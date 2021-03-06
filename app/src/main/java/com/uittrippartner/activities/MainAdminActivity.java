package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.uittrippartner.R;
import com.uittrippartner.fragments.BannerFragment;
import com.uittrippartner.fragments.BookingPartnerFragment;
import com.uittrippartner.fragments.ParticipantFragment;
import com.uittrippartner.fragments.PartnerFragment;
import com.uittrippartner.fragments.RoomFragment;
import com.uittrippartner.fragments.StatisticFragment;
import com.uittrippartner.fragments.VoucherFragment;

public class MainAdminActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        bottom_navigation = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new ParticipantFragment(), null).commit();

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.pageParticipants:
                        fragment = new ParticipantFragment();
                        break;
                    case R.id.pageBanners:
                        fragment = new BannerFragment();
                        break;
                    case R.id.pageVouchers:
                        fragment = new VoucherFragment();
                        break;
                    case R.id.btnLogOut:{
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainAdminActivity.this);
                        alertdialog.setTitle("Th??ng b??o");
                        alertdialog.setMessage("B???n c?? ch???c ch???n mu???n ????ng xu???t kh??ng?");
                        alertdialog.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainAdminActivity.this, LoginActivity.class));
                            }
                        });
                        alertdialog.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                        alertdialog.show();
                        break;

                    }
                }

                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_container, fragment).commit();
                }

                return true;
            }
        });

    }
}