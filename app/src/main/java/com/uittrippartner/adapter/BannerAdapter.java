package com.uittrippartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private Context mContext;
    List<Banner> mList;

    public BannerAdapter(Context mContext) {
        this.mContext = mContext;
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
        Glide.with(mContext).load(mList.get(position).getImage()).into(holder.imgBanner);
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
