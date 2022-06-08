package com.uittrippartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Participant;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private Context mContext;
    private List<Participant> participantList;

    public ParticipantAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addList(List<Participant> list){
        participantList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant,parent,false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        holder.txtName.setText(participantList.get(position).getName());

        Glide.with(mContext).load(participantList.get(position).getUrlImage()).placeholder(R.drawable.applogo).into(holder.img);
    }

    @Override
    public int getItemCount() {
        if(participantList == null)
            return 0;

        return participantList.size();
    }



    public class ParticipantViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        CircleImageView img;
        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            img = itemView.findViewById(R.id.img);
        }
    }
}
