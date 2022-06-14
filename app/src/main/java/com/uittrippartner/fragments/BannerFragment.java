package com.uittrippartner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uittrippartner.R;
import com.uittrippartner.activities.AddBannerActivity;
import com.uittrippartner.adapter.PhotoAdapter;
import com.uittrippartner.hotel.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerFragment extends Fragment {

    ImageSlider image_slider;
    List<Banner> bannerList, bannerListNew;
    FirebaseFirestore db;
    List<String> listImg ;
    PhotoAdapter photoAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button btnAdd;
    boolean hasCreated = false;
    Banner banner;
    String idBanner;

    public BannerFragment() {
        // Required empty public constructor
    }

    public static BannerFragment newInstance(String param1, String param2) {
        return new BannerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoAdapter = new PhotoAdapter(getContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://travel-81548.appspot.com");
        db = FirebaseFirestore.getInstance();
        bannerListNew = new ArrayList<>();
        listImg = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        image_slider = view.findViewById(R.id.image_slider);

        bannerList = new ArrayList<>();

        db.collection("banners").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() == 0)
                    return;

                List<SlideModel> slideModelList = new ArrayList<>();

                if(task.getResult().size() > 0){
                    btnAdd.setText("Sửa banner");
                    image_slider.setVisibility(View.VISIBLE);
                    hasCreated = true;
                }

                for (DocumentSnapshot doc : task.getResult()) {
                    idBanner = doc.getId();
                    banner = doc.toObject(Banner.class);
                }

                for(String s : banner.getImages()){
                    SlideModel slideModel = new SlideModel(s, null, ScaleTypes.CENTER_CROP);
                    slideModelList.add(slideModel);
                }

                image_slider.setImageList(slideModelList);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCreated){
                    Intent intent = new Intent(getContext(),AddBannerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("banner",banner);
                    bundle.putString("idBanner",idBanner);
                    bundle.putBoolean("hasCreated",true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(getContext(),AddBannerActivity.class));
                }
            }
        });

    }
}