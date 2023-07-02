package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.beginagain.Model.UserModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.example.beginagain.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToanActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTongTien, txtSdt, txtEmail;
    private EditText edtDiaChi;
    private AppCompatButton btnDatHang;
    private Disposable disposable;
    private long tongTien;
    private int totalItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        countItem();
        initControl();
    }

    private void initControl() {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongTien = getIntent().getLongExtra("tongtien", 0);
        txtTongTien.setText(decimalFormat.format(tongTien));
        txtEmail.setText(Utils.user_current.getEmail());
        txtSdt.setText(Utils.user_current.getMobile());
        edtDiaChi.setText(Utils.user_current.getDiachi());

        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edtDiaChi.getText().toString().trim();
                if (TextUtils.isEmpty(str_diachi)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                } else {
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.setLenient().create();
                    String str_chi_tiet = gson.toJson(Utils.mangmuahang);

                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    //new Gson().toJson(Utils.mangmuahang)
                    Log.d("test", str_chi_tiet);
                    ApiShop.getApiShop.createOrder(str_email, str_sdt, String.valueOf(tongTien), id, str_diachi, totalItem, str_chi_tiet)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<UserModel>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    disposable = d;
                                }

                                @Override
                                public void onNext(@NonNull UserModel userModel) {

                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Log.d("ThanhToanActivity", e.getMessage());
                                    Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                    Utils.mangmuahang.clear();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }
        });
    }

    private void countItem() {
        totalItem = 0;
        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        txtTongTien = findViewById(R.id.txttongtien);
        txtSdt = findViewById(R.id.txtsodienthoai);
        txtEmail = findViewById(R.id.txtemail);
        edtDiaChi = findViewById(R.id.edtdiachi);
        btnDatHang = findViewById(R.id.btndathang);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }
}