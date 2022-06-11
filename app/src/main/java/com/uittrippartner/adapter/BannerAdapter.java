package com.uittrippartner.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.uittrippartner.IClickItemBannerListener;
import com.uittrippartner.R;
import com.uittrippartner.activities.AddBannerActivity;
import com.uittrippartner.hotel.Banner;

import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private Context mContext;
    List<Banner> mList;
    private IClickItemBannerListener iClickItemBannerListener;

    public BannerAdapter(Context mContext, IClickItemBannerListener listener) {
        this.mContext = mContext;
        this.iClickItemBannerListener = listener;
    }

    public void setList(List<Banner> list){
        mList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_banner,parent,false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        final Banner banner = mList.get(position);
        if(banner == null)
            return;
        Glide.with(mContext).load(mList.get(position).getImage()).into(holder.imgBanner);
        holder.imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(mContext, holder.imgBanner);
//                popupMenu.inflate(R.menu.menu_banner);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()) {
//                            case R.id.itemEditBanner:
//                                //handle menu1 click
//                                break;
//                            case R.id.itemDeleteBanner:
//
//                                break;
//                        }
//
//                        return false;
//                    }
//                });
//                popupMenu.show();
                iClickItemBannerListener.onClickItemBanner(banner, holder.getAdapterPosition(), mList.get(position).getImage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder{

        ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }



}
