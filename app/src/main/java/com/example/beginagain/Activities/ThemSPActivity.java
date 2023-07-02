package com.example.beginagain.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSPActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinner;
    private TextInputEditText tenSp, giaSp, hinhAnh, moTa;
    private AppCompatButton btnThem;
    private ImageView imgCamera;
    private Disposable disposable;
    private SanPhamMoi sanPhamSua;
    private boolean flag = false;
    private int loai = 0;
    private String mediaPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_sp);
        initView();
        ActionToolbar();
        initControl();
        Intent intent = getIntent();
        sanPhamSua = (SanPhamMoi) intent.getSerializableExtra("sua");
        if (sanPhamSua == null) {
            flag = false;
        } else {
            flag = true;
            btnThem.setText("Sửa sản phẩm");
            tenSp.setText(sanPhamSua.getTensp());
            giaSp.setText(sanPhamSua.getGiasp());
            hinhAnh.setText(sanPhamSua.getHinhanh());
            moTa.setText(sanPhamSua.getMota());
            spinner.setSelection(sanPhamSua.getLoai());
        }

    }

    private void initControl() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false) {
                    themSanPham();
                } else {
                    suaSanPham();
                }
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ThemSPActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }

    private void suaSanPham() {
        String str_ten = tenSp.getText().toString().trim();
        String str_gia = giaSp.getText().toString().trim();
        String str_hinhanh = hinhAnh.getText().toString().trim();
        String str_mota = moTa.getText().toString().trim();

        if (TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) ||
                TextUtils.isEmpty(str_hinhanh) || TextUtils.isEmpty(str_mota) || loai == 0) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else {
            ApiShop.getApiShop.updateSp(str_ten, str_gia, str_hinhanh, str_mota, loai, sanPhamSua.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MessageModels>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(@NonNull MessageModels messageModels) {
                            if (messageModels.isSuccess()) {
                                Toast.makeText(getApplicationContext(), messageModels.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), messageModels.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("ThemSPActivity", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("ThemSPActivity", "Sửa thành công");
                            ThemSPActivity.this.finish();
                        }
                    });
        }

    }

    private void themSanPham() {
        String str_ten = tenSp.getText().toString().trim();
        String str_gia = giaSp.getText().toString().trim();
        String str_hinhanh = hinhAnh.getText().toString().trim();
        String str_mota = moTa.getText().toString().trim();

        if (TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) ||
                TextUtils.isEmpty(str_hinhanh) || TextUtils.isEmpty(str_mota) || loai == 0) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else {
            ApiShop.getApiShop.insertSp(str_ten, str_gia, str_hinhanh, str_mota, loai)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MessageModels>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(@NonNull MessageModels messageModels) {
                            if (messageModels.isSuccess()) {
                                Toast.makeText(getApplicationContext(), messageModels.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), messageModels.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("ThemSPActivity", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("ThemSPActivity", "Thêm thành công");
                            ThemSPActivity.this.finish();
                        }
                    });
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        spinner = findViewById(R.id.spinner_loai);
        tenSp = findViewById(R.id.tensp);
        giaSp = findViewById(R.id.giasp);
        hinhAnh = findViewById(R.id.hinhanh);
        imgCamera = findViewById(R.id.imgcamera);
        moTa = findViewById(R.id.mota);
        btnThem = findViewById(R.id.btnthem);

        List<String> stringList = new ArrayList<>();
        stringList.add("Vui lòng chọn loại");
        stringList.add("Quần Áo");
        stringList.add("Phụ kiện");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadMultipleFiles();
        Log.d("ThemSPActivity", "onActivityResult: " + mediaPath);
    }

    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<MessageModels> call = ApiShop.getApiShop.uploadFile(fileToUpload);
        call.enqueue(new Callback<MessageModels>() {
            @Override
            public void onResponse(Call<MessageModels> call, Response<MessageModels> response) {
                MessageModels serverRespone = response.body();
                if (serverRespone != null) {
                    if (serverRespone.isSuccess()) {
                        hinhAnh.setText(serverRespone.getName()+"");
                    } else {
                        Log.d("Response", serverRespone.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageModels> call, Throwable t) {
                Log.d("log", "uploadMultipleFiles "+t.getMessage());
            }
        });


    }

    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
