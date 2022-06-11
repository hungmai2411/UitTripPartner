package com.uittrippartner.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.uittrippartner.IClickItemBannerListener;
import com.uittrippartner.R;
import com.uittrippartner.activities.AddBannerActivity;
import com.uittrippartner.activities.AddRoomActivity;
import com.uittrippartner.activities.AddVoucherActivity;
import com.uittrippartner.activities.MainAdminActivity;
import com.uittrippartner.adapter.BannerAdapter;
import com.uittrippartner.adapter.PhotoAdapter;
import com.uittrippartner.adapter.VoucherAdapter;
import com.uittrippartner.hotel.Banner;
import com.uittrippartner.hotel.Voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import vn.thanguit.toastperfect.ToastPerfect;

public class BannerFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView rcvBanners;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> listImg = new ArrayList<>();
    PhotoAdapter photoAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((MainAdminActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainAdminActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvBanners = view.findViewById(R.id.rcvBanners);
        bannerAdapter = new BannerAdapter(getContext(), new IClickItemBannerListener() {
            @Override
            public void onClickItemBanner(Banner banner, int position, String url) {
                PopupMenu popupMenu = new PopupMenu(getContext(), rcvBanners.getChildAt(position));
                popupMenu.inflate(R.menu.menu_banner);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.itemEditBanner:
                            {
                                requestPermission();
                                saveToStorage(url);
                                break;
                            }
                            case R.id.itemDeleteBanner:
                            {
                                break;
                            }

                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        bannerList = new ArrayList<>();

        db.collection("banners").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Banner banner = new Banner();
                    banner.setImage(doc.getString("image"));
                    bannerList.add(banner);
                }
                bannerAdapter.notifyDataSetChanged();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvBanners.setLayoutManager(linearLayoutManager);
        bannerAdapter.setList(bannerList);
        rcvBanners.setAdapter(bannerAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_voucher, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.ic_add) {
            startActivity(new Intent(getContext(), AddBannerActivity.class));
        }

        return true;
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectImagesFromGallery() {
        TedBottomPicker.with(this.getActivity())
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Xong")
                .setEmptySelectionText("Chưa chọn hình ảnh")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null && !uriList.isEmpty()) {
                            for (Uri uri : uriList) {
                                listImg.add(uri.toString());
                            }
                            photoAdapter.addData(listImg);
                        }
                    }
                });
    }

    private void saveToStorage(String imageURL) {
        for (String s : listImg) {
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

                                        saveToFireStore(s, imageURL);
                                    }
                                });
                            }
                        });
            }
        }
    }

    private void saveToFireStore(String uri, String url) {
        String url1 = url;
        HashMap<String, Object> map = new HashMap<>();
        map.put("image", uri);

        QuerySnapshot querySnapshot = db.collection("banners").whereEqualTo("image", url).get().getResult();
        for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
            String imageID = documentSnapshot.getId();

        boolean doc = db.collection("banners").document().getId().equals(url);

            db.collection("banners").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        if(doc.getId().equals(imageID)){
                            db.collection("banners").document(imageID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                }
            });
        }
//        db.collection("banners").document(imageID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(getContext(), "Update successfully", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

}