package com.uittrippartner.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.uittrippartner.fragments.PartnerFragment;
import com.uittrippartner.fragments.UserFragment;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PartnerFragment();
            case 1:
                return new UserFragment();
        }
        return new UserFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
