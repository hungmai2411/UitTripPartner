package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.uittrippartner.R;
import com.uittrippartner.RetrofitInstance;
import com.uittrippartner.SendMessageApi;
import com.uittrippartner.adapter.PhotoAdapter;
import com.uittrippartner.hotel.Data;
import com.uittrippartner.hotel.Message;
import com.uittrippartner.hotel.room.Photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.thanguit.toastperfect.ToastPerfect;

public class AddBannerActivity extends AppCompatActivity {
    Button btnAdd,btnSelectImages;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;
    ExecutorService executorService;
    ProgressDialog progressDialog;
    List<String> listTmp = new ArrayList<>();
    RecyclerView rcvImages;
    private FirebaseStorage storage;
    StorageReference storageReference;
    PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banner);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://travel-81548.appspot.com");
        executorService = Executors.newSingleThreadExecutor();
        toolbar = findViewById(R.id.toolbar);
        btnAdd = findViewById(R.id.btnAdd);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        photoAdapter = new PhotoAdapter(this);
        rcvImages = findViewById(R.id.rcvImages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvImages.setLayoutManager(linearLayoutManager);
        rcvImages.setAdapter(photoAdapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AddBannerActivity.this);

//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        saveToFireStore(s);
//                    }
//                }, 5000);

                if(listTmp.isEmpty()){
                    ToastPerfect.makeText(AddBannerActivity.this, ToastPerfect.BOTTOM, "Chưa chọn hình ảnh", ToastPerfect.WARNING, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }else{
                    for (String s : listTmp) {
                        if (s != null) {
                            Uri uri = Uri.parse(s);

                            StorageReference riversRef = storageReference.child("banners/" + uri);

                            riversRef.putFile(uri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String s = uri.toString();

                                                    saveToFireStore(s);
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
    }

    private void saveToFireStore(String s) {
        String imageID = String.format(Locale.US, "%d.jpeg", System.currentTimeMillis());
        HashMap<String, Object> map = new HashMap<>();
        map.put("image", s);
        map.put("imageID", imageID);

        db.collection("banners").document(imageID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dismissDialog();
                ToastPerfect.makeText(AddBannerActivity.this, ToastPerfect.BOTTOM, "Thêm banner thành công", ToastPerfect.SUCCESS, Toast.LENGTH_SHORT).show();
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

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddBannerActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectImagesFromGallery() {
        TedBottomPicker.with(AddBannerActivity.this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Xong")
                .setEmptySelectionText("Chưa chọn hình ảnh")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null && !uriList.isEmpty()) {
                            for (Uri uri : uriList){
                                listTmp.add(uri.toString());
                            }
                            photoAdapter.addData(listTmp);
                        }
                    }
                });
    }
}