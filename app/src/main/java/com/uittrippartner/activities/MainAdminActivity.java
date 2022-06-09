package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, fragment).commit();

                return true;
            }
        });

    }
}