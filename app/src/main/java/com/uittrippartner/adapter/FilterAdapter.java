package com.uittrippartner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uittrippartner.ItemClickListener;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Common;
import com.uittrippartner.hotel.Sort;

import java.util.List;

class FilterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtName;
    public ImageView btnCheck;
    public RelativeLayout item;
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FilterViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txtName);
        btnCheck = itemView.findViewById(R.id.btnCheck);
        item = itemView.findViewById(R.id.item);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition());
    }
}

public class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {
    private Context mContext;
    private int rowIndex = 0;
    private List<Sort> mList;
    private IClickSortItem iClickSortItem;

    public FilterAdapter(Context mContext, IClickSortItem iClickSortItem) {
        this.mContext = mContext;
        this.iClickSortItem = iClickSortItem;
    }

    public void setData(List<Sort> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sort, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        //holder.bind(position);
        if(Common.currentItem != null) {
            rowIndex = Common.currentItem.getIndex();
        }
        holder.txtName.setText(mList.get(position).getName());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position == 0){
                    mList.get(position).setChecked(true);
                    mList.get(position).setIndex(0);
                    mList.get(1).setChecked(false);
                    mList.get(2).setChecked(false);
                }else if(position == 1){
                    mList.get(position).setChecked(true);
                    mList.get(0).setChecked(false);
                    mList.get(2).setChecked(false);
                    mList.get(position).setIndex(1);
                }else if(position == 2){
                    mList.get(position).setChecked(true);
                    mList.get(1).setChecked(false);
                    mList.get(0).setChecked(false);
                    mList.get(position).setIndex(2);
                }
                rowIndex = position;
                Common.currentItem = mList.get(position);
                iClickSortItem.onCallBack(holder.txtName.getText().toString());
                notifyDataSetChanged();
            }
        });

        if(rowIndex == position){
            holder.btnCheck.setVisibility(View.VISIBLE);
            holder.item.setBackgroundResource(R.drawable.custom_linear);
        }else{
            holder.btnCheck.setVisibility(View.GONE);
            holder.item.setBackgroundResource(R.drawable.custom_linear_1);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface IClickSortItem {
        void onCallBack(String choice);
    }
}

