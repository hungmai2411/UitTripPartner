package com.uittrippartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uittrippartner.R;
import com.uittrippartner.hotel.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private Context mContext;
    private List<Notification> mListNotification;

    public NotificationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDate(List<Notification> list){
        mListNotification = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notification,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        String strDate = dateFormat.format(mListNotification.get(position).getDate());

        holder.txtDate.setText(strDate);
    }

    @Override
    public int getItemCount() {
        if(mListNotification == null){
            return 0;
        }

        return mListNotification.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView txtDate;
        ImageView imgIcon;
        TextView txtDescription;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}

