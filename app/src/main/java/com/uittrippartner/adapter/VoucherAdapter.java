package com.uittrippartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uittrippartner.R;
import com.uittrippartner.hotel.Voucher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private Context mContext;
    private List<Voucher> voucherList;

    public VoucherAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addList(List<Voucher> list){
        voucherList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        holder.txtTitle.setText(String.valueOf(voucherList.get(position).getNumber()));
        holder.txtCode.setText(voucherList.get(position).getCode());

        Date date = voucherList.get(position).getEndDate();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        holder.txtHSD.setText(strDate);
        holder.txtDescription.setText(voucherList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if(voucherList == null)
            return 0;
        return voucherList.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtDescription,txtHSD,txtCode;


        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtHSD = itemView.findViewById(R.id.txtHSD);
        }
    }
}
