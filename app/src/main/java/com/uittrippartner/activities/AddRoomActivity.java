package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.uittrippartner.R;
import com.uittrippartner.adapter.PhotoAdapter;
import com.uittrippartner.hotel.room.Photo;
import com.uittrippartner.hotel.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import gun0912.tedbottompicker.TedRxBottomPicker;
import vn.thanguit.toastperfect.ToastPerfect;

public class AddRoomActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rcvImages;
    PhotoAdapter photoAdapter;
    Button btnSelectImages, btnAdd;
    EditText edtName, edtPrice, edtPolicy, edtFacility, edtSize, edtNumber;
    ProgressDialog progressDialog;
    private FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> listTmp = new ArrayList<>();
    ExecutorService executorService;
    Room room;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://travel-81548.appspot.com");
        executorService = Executors.newSingleThreadExecutor();
        edtNumber = findViewById(R.id.edtNumber);
        btnAdd = findViewById(R.id.btnAdd);
        edtFacility = findViewById(R.id.edtFacility);
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtPolicy = findViewById(R.id.edtPolicy);
        edtSize = findViewById(R.id.edtSize);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        rcvImages = findViewById(R.id.rcvImages);
        toolbar = findViewById(R.id.toolbar);
        photoAdapter = new PhotoAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvImages.setLayoutManager(linearLayoutManager);
        rcvImages.setAdapter(photoAdapter);

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();

        if (intent != null && intent.getExtras() != null) {
            room = (Room) intent.getExtras().getSerializable("room");
            btnAdd.setText("Cập nhật");
            check = 1;

            for(Photo photo : room.getPhotos()){
                listTmp.add(photo.getRoomImage());
            }

            photoAdapter.addData(listTmp);
            edtName.setText(room.getName());
            edtPolicy.setText(room.getCancelPolicies());
            edtFacility.setText(room.getFacilities());
            edtPrice.setText(String.valueOf(room.getPrice()));
            String size = room.getRoomArea().substring(0,room.getRoomArea().indexOf("m"));
            edtSize.setText(size);
            edtNumber.setText(String.valueOf(room.getNumber()));
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtName.getText().toString().matches("") || edtPolicy.getText().toString().matches("") || edtFacility.getText().toString().matches("")
                || edtPrice.getText().toString().matches("") || edtSize.getText().toString().matches("") || edtNumber.getText().toString().matches("")){
                    ToastPerfect.makeText(AddRoomActivity.this,ToastPerfect.ERROR,"Các trường thông tin phải được điền đầy đủ",ToastPerfect.BOTTOM, Toast.LENGTH_SHORT).show();
                }else{
                    String nameRoom = edtName.getText().toString();
                    String policy = edtPolicy.getText().toString();
                    String facility = edtFacility.getText().toString();
                    long price = Long.valueOf(edtPrice.getText().toString());
                    long size = Long.valueOf(edtSize.getText().toString());
                    long number = Long.valueOf(edtNumber.getText().toString());

                    // check cac input khac null

                    if (check == 0) {
                        showDialog(AddRoomActivity.this);

                        List<Photo> list = new ArrayList<>();

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveToFireStore(nameRoom, policy, facility, price, size, number, list);
                            }
                        }, 5000);

                        for (String s : listTmp) {
                            if (s != null) {
                                Uri uri = Uri.parse(s);

                                StorageReference riversRef = storageReference.child("rooms/" + uri);

                                riversRef.putFile(uri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Photo photo = new Photo();
                                                        photo.setRoomImage(uri.toString());
                                                        list.add(photo);
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }else{
                        List<Photo> list = new ArrayList<>();
                        showDialog(AddRoomActivity.this);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateFireStore(nameRoom, policy, facility, price, size, number, list);
                            }
                        }, 5000);

                        for (String s : listTmp) {
                            if (s != null) {
                                Uri uri = Uri.parse(s);

                                StorageReference riversRef = storageReference.child("rooms/" + uri);

                                riversRef.putFile(uri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Photo photo = new Photo();
                                                        photo.setRoomImage(uri.toString());
                                                        list.add(photo);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("err",e.toString());
                                        Photo photo = new Photo();
                                        photo.setRoomImage(uri.toString());
                                        list.add(photo);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateFireStore(String nameRoom, String policy, String facility, long price, long size, long number, List<Photo> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", nameRoom);
        map.put("price", price);
        map.put("roomArea", size + "m2");
        map.put("facilities", facility);
        map.put("cancelPolicies", policy);
        map.put("photos", list);
        map.put("number", number);

        db.collection("Hotels/" + 1428 + "/rooms").document(room.getId())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissDialog();
                        Toast.makeText(AddRoomActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("err",e.getMessage());
                    }
                });
    }
    private void saveToFireStore(String nameRoom, String policy, String facility, long price, long size, long number, List<Photo> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", nameRoom);
        map.put("price", price);
        map.put("roomArea", size + "m2");
        map.put("facilities", facility);
        map.put("cancelPolicies", policy);
        map.put("photos", list);
        map.put("number", number);

        db.collection("Hotels/" + 1428 + "/rooms").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                dismissDialog();
                ToastPerfect.makeText(AddRoomActivity.this,ToastPerfect.SUCCESS, "Thêm phòng thành công", ToastPerfect.BOTTOM,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddRoomActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectImagesFromGallery() {
        TedBottomPicker.with(AddRoomActivity.this)
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