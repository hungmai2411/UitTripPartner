package com.uittrippartner.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uittrippartner.HandleCurrency;
import com.uittrippartner.R;
import com.uittrippartner.hotel.room.Photo;
import com.uittrippartner.hotel.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context mContext;
    private List<Room> mList;
    FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    Dialog mDialog;
    private IClickRoomListener mIClickRoomListener;

    public RoomAdapter(Context mContext, Dialog dialog, IClickRoomListener iClickRoomListener) {
        this.mContext = mContext;
        this.mDialog = dialog;
        this.mIClickRoomListener = iClickRoomListener;
    }

    public void setData(List<Room> list){
        mList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room,parent,false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(mList == null)
            return;
        holder.txtTypeRoom.setText(mList.get(position).getName());
        Glide.with(mContext).load(mList.get(position).getPhotos().get(0).getRoomImage()).into(holder.imgRoom);
        holder.txtPrice.setText(new HandleCurrency().handle(mList.get(position).getPrice()));

        Button Okay = mDialog.findViewById(R.id.btn_okay);
        Button Cancel = mDialog.findViewById(R.id.btn_cancel);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();

                Okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFireStore.collection("Hotels/" + 1428 + "/rooms").document(String.valueOf(mList.get(position).getId()))
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        mDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                });

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFireStore.collection("Hotels/" + 1017 + "/rooms").document(String.valueOf(mList.get(position).getId()))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Room room = new Room();

                                List<Photo> list = (List<Photo>) document.get("photos");
                                List<Photo> listTmp = new ArrayList<>();

                                for (int i = 0; i < list.size(); i++) {
                                    final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                                    final Photo pojo = mapper.convertValue(list.get(i), Photo.class);
                                    listTmp.add(pojo);
                                }

                                room.setId(document.getId());
                                room.setName(document.getString("name"));
                                room.setCancelPolicies(document.getString("cancelPolicies"));
                                room.setFacilities(document.getString("facilities"));
                                room.setRoomArea(document.getString("roomArea"));

                                if (document.get("number") != null)
                                    room.setNumber((Long) document.get("number"));

                                room.setPhotos(listTmp);
                                room.setPrice((Long) document.get("price"));

                                mIClickRoomListener.onCallBack(room);
                            } else {
                            }
                        }
                    }
                });
            }
        });
    }

    public interface IClickRoomListener{
        public void onCallBack(Room room);
    }
    @Override
    public int getItemCount() {
        if(mList == null)
            return 0;
        return mList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{
        TextView txtTypeRoom;
        TextView txtPrice;
        ShapeableImageView imgRoom;
        LinearLayout layout;
        ImageView btnEdit,btnDelete;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtTypeRoom = itemView.findViewById(R.id.txtType);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
