package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.HandleCurrency;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Booking;
import com.uittrippartner.hotel.Review;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BookingDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTitle,txtPhoneNumber,txtIdBooking,txtTypeRoom, txtNumber,txtDate,txtPrice,txtSale;
    Button btnCheckOut,btnViewReview;
    Long endDate;
    ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Booking booking;
    Review review;
    String idReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        btnViewReview = findViewById(R.id.btnViewReview);
        btnCheckOut = findViewById(R.id.btnCheckOut);
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
            booking = (Booking) intent.getExtras().getSerializable("booking");

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

            endDate = booking.getEndDate();
            Date now = new Date();
            try {
                String end = android.text.format.DateFormat.format("dd/MM/yyyy", new Date(endDate)).toString();
                Date dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(end));
                Date newDate = new Date(dateEnd.getTime() + 14 * 3600*1000);
                if(now.compareTo(newDate) == 1 && booking.getStatus().equals("Booked")){
                    btnCheckOut.setVisibility(View.VISIBLE);
                }else if(now.compareTo(newDate) == 0 && booking.getStatus().equals("Booked") && now.getHours() >= 14)
                    btnCheckOut.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(booking.getStatus().equals("Successfully")) {
            db.collection("Hotels/" + booking.getIdHotel() + "/reviews")
                    .whereEqualTo("idUser", booking.getIdUser())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                if (doc.get("bookingID").equals(booking.getIdBooking())) {
                                    review = doc.toObject(Review.class);
                                    idReview = doc.getId();
                                    btnViewReview.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        }

        btnViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(BookingDetailActivity.this,ReviewActivity.class);

                Bundle bundle = new Bundle();
                if(review != null) {
                    bundle.putSerializable("review", review);
                    intent1.putExtras(bundle);
                }
                startActivity(intent1);
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(BookingDetailActivity.this);

                HashMap<String,Object> hashMap = new HashMap<>();

                hashMap.put("status","Successfully");

                db.collection("Hotels/" + 1428 + "/booked")
                        .document(booking.getIdBooking())
                        .update(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dismissDialog();
                            }
                        });

                db.collection("users/" + booking.getIdUser() + "/booked")
                        .document(booking.getIdBooking())
                        .update(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            }
        });
    }

    public void showDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialog() {
        progressDialog.dismiss();
    }
}