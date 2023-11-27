package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beginagain.Adapter.SanPhamMoiAdapter;
import com.example.beginagain.Model.EventBus.SuaXoaEvent;
import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.Model.SanPhamMoiModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.example.beginagain.Retrofit.RetrofitService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLiActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgThem;
    private RecyclerView recyclerView;
    private Disposable disposable;
    private List<SanPhamMoi> list;
    private SanPhamMoi sanPhamSuaXoa;
    private SanPhamMoiAdapter adapter;
    private ApiShop apiShop;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_li);

        retrofitService = new RetrofitService();
        apiShop = retrofitService.getRetrofit().create(ApiShop.class);

        initView();
        ActionToolbar();
        initControl();
        getSpMoi();
    }

    private void getSpMoi() {
        apiShop.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SanPhamMoiModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull SanPhamMoiModel sanPhamMoiModel) {
                        if (sanPhamMoiModel.isSuccess()) {
                            list = sanPhamMoiModel.getResult();
                            adapter = new SanPhamMoiAdapter(getApplicationContext(), list);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("QuanLiActivity", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("QuanLiActivity", "Lấy DL xong");
                    }
                });
    }

    private void initControl() {
        imgThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ThemSPActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        imgThem = findViewById(R.id.img_them);
        recyclerView = findViewById(R.id.recycleview_ql);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onContextItemSelected(@androidx.annotation.NonNull MenuItem item) {
        if (item.getTitle().equals("Sửa")) {
            suaSanPham();
        } else if (item.getTitle().equals("Xóa")) {
            xoaSanPham();
        }
        return super.onContextItemSelected(item);
    }

    private void xoaSanPham() {
        apiShop.xoaSanPham(sanPhamSuaXoa.getId())
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
                        Log.d("QuanLiActivity", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("QuanLiActivity", "Xóa sản phẩm thành công");
                    }
                });
    }

    private void suaSanPham() {
        Intent intent = new Intent(getApplicationContext(), ThemSPActivity.class);
        intent.putExtra("sua", sanPhamSuaXoa);
        startActivity(intent);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventSuaXoa(SuaXoaEvent event) {
        if (event != null) {
            sanPhamSuaXoa = event.getSanPhamMoi();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null)
            disposable.dispose();
    }
}