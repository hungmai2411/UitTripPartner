package com.uittrippartner.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.R;
import com.uittrippartner.RetrofitInstance;
import com.uittrippartner.SendMessageApi;
import com.uittrippartner.hotel.Data;
import com.uittrippartner.hotel.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.thanguit.toastperfect.ToastPerfect;

public class AddVoucherActivity extends AppCompatActivity {
    Button btnAdd;
    EditText edtCodeVoucher,edtNumber,edtDescription,edtEndDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatePickerDialog datePickerDialog;
    Toolbar toolbar;
    SendMessageApi sendMessageApi;
    ExecutorService executorService;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher);

        executorService = Executors.newSingleThreadExecutor();
        sendMessageApi = RetrofitInstance.retrofit.create(SendMessageApi.class);
        toolbar = findViewById(R.id.toolbar);
        btnAdd = findViewById(R.id.btnAdd);
        edtCodeVoucher = findViewById(R.id.edtCodeVoucher);
        edtNumber = findViewById(R.id.edtNumber);
        edtDescription = findViewById(R.id.edtDescription);
        edtEndDate = findViewById(R.id.edtEndDate);

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddVoucherActivity.this,
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            edtEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            datePickerDialog.dismiss();
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AddVoucherActivity.this);
                String code = edtCodeVoucher.getText().toString();
                long number = Long.parseLong(edtNumber.getText().toString());
                String description = edtDescription.getText().toString();
                String endDate = edtEndDate.getText().toString();

                HashMap<String,Object> hashMap = new HashMap<>();

                hashMap.put("code",code);
                hashMap.put("number",number);
                hashMap.put("description",description);
                hashMap.put("endDate",endDate);

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.collection("users").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot doc : task.getResult()){
                                            Message message = new Message();
                                            Data data = new Data();
                                            data.setUserName("Uit Trip Notification");
                                            data.setDescription("Bạn vừa nhận được một voucher");
                                            message.setPriority("high");
                                            message.setData(data);
                                            message.setTo(doc.getString("token"));

                                            Call<Message> repos = sendMessageApi.sendMessage(message);
                                            repos.enqueue(new Callback<Message>() {
                                                @Override
                                                public void onResponse(Call<Message> call, Response<Message> response) {
                                                    if (response.body() != null) {
                                                        db.collection("vouchers").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                ToastPerfect.makeText(AddVoucherActivity.this,"Thêm thành công", Toast.LENGTH_SHORT);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Message> call, Throwable t) {
                                                    Log.d("Confirm1Activity",t.getMessage().toString());
                                                }
                                            });

                                            HashMap<String,Object> notiMap = new HashMap<>();
                                            notiMap.put("timestamp", FieldValue.serverTimestamp());
                                            notiMap.put("type","voucher");
                                            notiMap.put("hasSeen",false);
                                            db.collection("users/" + doc.getId() + "/notifications")
                                                    .add(notiMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    dismissDialog();
                                                }
                                            });

                                            db.collection("users/" + doc.getId() + "/vouchers")
                                                    .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                }
                                            });

                                        }
                                    }
                                });
                    }
                });

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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