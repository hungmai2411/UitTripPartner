package com.uittrippartner.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.uittrippartner.IClickItemBannerListener;
import com.uittrippartner.R;
import com.uittrippartner.activities.AddBannerActivity;
import com.uittrippartner.activities.LoginActivity;
import com.uittrippartner.activities.MainAdminActivity;
import com.uittrippartner.adapter.BannerAdapter;
import com.uittrippartner.adapter.PhotoAdapter;
import com.uittrippartner.hotel.Banner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class BannerFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView rcvBanners;
    BannerAdapter bannerAdapter, bannerAdapterNew;
    List<Banner> bannerList, bannerListNew;
    FirebaseFirestore db;
    List<String> listImg ;
    PhotoAdapter photoAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    String url;

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

        toolbar = view.findViewById(R.id.toolbar);
        ((MainAdminActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainAdminActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rcvBanners = view.findViewById(R.id.rcvBanners);
        bannerAdapter = new BannerAdapter(getContext(), new IClickItemBannerListener() {
            @Override
            public void onClickItemBanner(Banner banner, int position) {
                PopupMenu popupMenu = new PopupMenu(getContext(), rcvBanners.getChildAt(position));
                popupMenu.inflate(R.menu.menu_banner);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.itemEditBanner:
                            {
                                requestPermission(bannerList.get(position).getImageID());
                                break;
                            }
                            case R.id.itemDeleteBanner:
                            {
                                deleteBanner(position);
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
                    banner.setImageID(doc.getString("imageID"));
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

    private void requestPermission(String id) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery(id);
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

    private void selectImagesFromGallery(String id) {
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
                                String s = uri.toString();
                                listImg.add(s);
                            }
                            photoAdapter.addData(listImg);

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
                                                            url = uri.toString();
                                                            HashMap<String, Object> map = new HashMap<>();
                                                            map.put("image", url);

                                                            db.collection("banners").document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(getContext(), "Edit banner successfully", Toast.LENGTH_SHORT).show();
                                                                    refresh();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }
    private void refresh(){
        Fragment banner = new BannerFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_container, banner).commit();
    }
    
    private void deleteBanner(int position){
        String id = bannerList.get(position).getImageID();

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
        alertdialog.setTitle("Thông báo");
        alertdialog.setMessage("Bạn có chắc chắn muốn xoá không?");
        alertdialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("banners").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Delete banner successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertdialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alertdialog.show();
    }

}