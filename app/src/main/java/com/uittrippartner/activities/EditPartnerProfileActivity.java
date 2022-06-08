package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uittrippartner.R;
import com.uittrippartner.fragments.PartnerProfileFragment;

import java.io.File;
import java.util.HashMap;

import vn.thanguit.toastperfect.ToastPerfect;

public class EditPartnerProfileActivity extends AppCompatActivity {
    ImageView btnAdd, imgAvatar;
    Uri avatarUri;
    EditText edtName, edtEmail, edtPhonenumber, edtAddress, edtFacebook, edtWebsite;
    String partnerID;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String avatarURL;
    Button btnUpdate;
    String name, email, phonenumber, address, fb, website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_partner_profile);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnAdd = (ImageView) findViewById(R.id.imgAdd);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        edtName = (EditText)findViewById(R.id.edtName);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPhonenumber = (EditText)findViewById(R.id.edtPhoneNumber);
        edtAddress = (EditText)findViewById(R.id.edtAddress);
        edtFacebook = (EditText)findViewById(R.id.edtFacebook);
        edtWebsite = (EditText)findViewById(R.id.edtWebsite);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                email = edtEmail.getText().toString();
                phonenumber = edtPhonenumber.getText().toString();
                address = edtAddress.getText().toString();
                fb = edtFacebook.getText().toString();
                website = edtWebsite.getText().toString();

                uploadPicture();
            }
        });

        setPartnerInformation();
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            avatarUri = data.getData();
            imgAvatar.setImageURI(avatarUri);
        }
    }

    public void setPartnerInformation(){
        FirebaseUser partner = FirebaseAuth.getInstance().getCurrentUser();
        if(partner != null){
            partnerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        firestore.collection("partners").document(partnerID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String email = task.getResult().getString("email");
                        String phoneNumber = task.getResult().getString("phonenumber");
                        String address = task.getResult().getString("address");
                        String fb = task.getResult().getString("facebook");
                        String avatar = task.getResult().getString("avatar");
                        String website = task.getResult().getString("website");

                        edtName.setText(name);
                        edtEmail.setText(email);
                        edtFacebook.setText(name);
                        edtPhonenumber.setText(phoneNumber);
                        edtAddress.setText(address);
                        edtFacebook.setText(fb);
                        edtWebsite.setText(website);
                        if(avatar == null || avatar == ""){

                        }else{
                            Glide.with(EditPartnerProfileActivity.this).load(avatar).into(imgAvatar);
                        }
                    }
                }
            }
        });
    }

    public void uploadPicture(){
        final String partnerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference Ref = storageReference.child("partnerAvatar/" + partnerID);

        if(avatarUri != null){
            Ref.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            avatarURL = uri.toString();
                            uploadInformation(name, email, address, phonenumber, fb, website, avatarURL);
                        }
                    });
                }
            });
        }else{
            firestore.collection("partners").document(partnerID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    avatarURL = documentSnapshot.getString("avatar");
                    uploadInformation(name, email, address, phonenumber, fb, website, avatarURL);
                }
            });
        }
    }

    public void uploadInformation(String name, String email, String address, String phonenumber, String fb, String website, String avatarURL){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading" + "...");
        pd.show();

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("address", address);
        map.put("phonenumber", phonenumber);
        map.put("avatar", avatarURL);
        map.put("facebook", fb);
        map.put("website", website);
        firestore.collection("partners").document(partnerID).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ToastPerfect.makeText(EditPartnerProfileActivity.this, ToastPerfect.SUCCESS, "Update information successfully", ToastPerfect.BOTTOM, ToastPerfect.LENGTH_SHORT).show();
                    finish();
                    pd.dismiss();
                } else{
                    ToastPerfect.makeText(EditPartnerProfileActivity.this, ToastPerfect.ERROR, task.getException().toString(), ToastPerfect.BOTTOM, ToastPerfect.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                ToastPerfect.makeText(EditPartnerProfileActivity.this, ToastPerfect.ERROR, "Failed to update information", ToastPerfect.BOTTOM, ToastPerfect.LENGTH_SHORT).show();
            }
        });
    }
}