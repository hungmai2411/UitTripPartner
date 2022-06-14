package com.uittrippartner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uittrippartner.HandleCurrency;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Booking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingPartnerAdapter extends RecyclerView.Adapter<BookingPartnerAdapter.BookingPartnerViewHolder> {
    private List<Booking> bookingList;
    private IClickBookingListener iClickBookingListener;

    public BookingPartnerAdapter(Context mContext, IClickBookingListener iClickBookingListener) {
        this.mContext = mContext;
        this.iClickBookingListener = iClickBookingListener;
    }

    public void addData(List<Booking> list){
        bookingList = list;
        notifyDataSetChanged();
    }

    private Context mContext;

    @NonNull
    @Override
    public BookingPartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_partner,parent,false);
        return new BookingPartnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingPartnerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(bookingList == null)
            return;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickBookingListener.onCallBack(bookingList.get(position));
            }
        });

        Date date = bookingList.get(position).getDate();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        holder.txtNumberNight.setText(String.valueOf(bookingList.get(position).getDaysdiff()) + " night");
        holder.txtPrice.setText(new HandleCurrency().handle(bookingList.get(position).getPrice()));
        holder.txtPaymentMethod.setText(bookingList.get(position).getChoice());
        holder.txtNameRoom.setText(bookingList.get(position).getNameRoom());
        holder.txtDateBooking.setText(strDate);
        holder.txtIdBooking.setText(bookingList.get(position).getIdBooking());
        holder.txtPhoneNumber.setText(bookingList.get(position).getPhonenumber());

        if(bookingList.get(position).getStatus().equals("Cancelled")){
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.cancelled_text));
            holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.cancelled_color));
        }else{
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.booked_text));
            holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.booked_color));
        }
        holder.txtStatus.setText(bookingList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        if(bookingList == null)
            return 0;

        return bookingList.size();
    }

    public class BookingPartnerViewHolder extends RecyclerView.ViewHolder{
        TextView txtIdBooking,txtPhoneNumber,txtNameRoom,txtNumberNight,txtPaymentMethod,txtPrice,txtDateBooking,txtStatus;

        public BookingPartnerViewHolder(@NonNull View itemView) {
            super(itemView);

            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDateBooking = itemView.findViewById(R.id.txtDateBooking);
            txtIdBooking = itemView.findViewById(R.id.txtIdBooking);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            txtNameRoom = itemView.findViewById(R.id.txtNameRoom);
            txtNumberNight = itemView.findViewById(R.id.txtNumberNight);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }

    public interface IClickBookingListener{
        public void onCallBack(Booking booking);
    }
}