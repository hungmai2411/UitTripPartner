package com.uittrippartner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uittrippartner.R;
import com.uittrippartner.adapter.PhotoReviewAdapter;
import com.uittrippartner.hotel.Review;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rcvImages;
    TextView txtReview;
    TextView txtName,txtNumStar;
    CircleImageView imgUser;
    Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();

        if (intent != null) {

            Bundle bundle = intent.getExtras();

            if(bundle != null){
                review = (Review) bundle.getSerializable("review");
            }
        }

        toolbar = findViewById(R.id.toolbar);

        rcvImages =findViewById(R.id.rcvImages);
        txtReview = findViewById(R.id.txtReview);
        txtName = findViewById(R.id.txtName);
        txtNumStar = findViewById(R.id.txtNumStar);
        imgUser = findViewById(R.id.imgUser);

        txtName.setText(review.getNameUser());
        txtNumStar.setText(String.valueOf(review.getRate()));
        txtReview.setText(review.getReview());

        PhotoReviewAdapter photoAdapter;

        photoAdapter = new PhotoReviewAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvImages.setLayoutManager(linearLayoutManager);
        photoAdapter.addData(review.getImages());
        rcvImages.setAdapter(photoAdapter);
        Glide.with(this).load(review.getImgUser()).into(imgUser);
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}