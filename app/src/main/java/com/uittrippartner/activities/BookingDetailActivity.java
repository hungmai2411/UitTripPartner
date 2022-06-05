package com.uittrippartner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uittrippartner.HandleCurrency;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Booking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTitle,txtPhoneNumber,txtIdBooking,txtTypeRoom, txtNumber,txtDate,txtPrice,txtSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        txtSale = findViewById(R.id.txtSale);
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txtTitle);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtIdBooking = findViewById(R.id.txtIdBooking);
        txtTypeRoom = findViewById(R.id.txtTypeRoom);
        txtNumber = findViewById(R.id.txtNumber);
        txtDate = findViewById(R.id.txtDate);
        txtPrice = findViewById(R.id.txtPrice);

        Intent intent = getIntent();

        if(intent != null){
            Booking booking = (Booking) intent.getExtras().getSerializable("booking");

            txtTitle.setText(booking.getIdBooking());
            txtIdBooking.setText(booking.getIdBooking());
            txtNumber.setText(String.valueOf(booking.getDaysdiff()) + " night");
            txtPrice.setText(new HandleCurrency().handle(booking.getPrice()));
            txtSale.setText(new HandleCurrency().handle(0));

            txtTypeRoom.setText(booking.getNameRoom());

            Date date = booking.getDate();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(date);

            txtDate.setText(strDate);
            txtPhoneNumber.setText(booking.getPhonenumber());
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}