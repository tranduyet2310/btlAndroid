package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.beginagain.Adapter.SanPhamAdapter;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.Model.SanPhamMoiModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PhuKienActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Disposable disposable;
    private SanPhamAdapter sanPhamAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Handler handler = new Handler();
    private int page = 2;
    private int loai;
    private boolean isLoading = false;
    private List<SanPhamMoi> sanPhamMoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phu_kien);
        loai = getIntent().getIntExtra("loai", 2);
        initView();
        ActionToolbar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@androidx.annotation.NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();

                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        isLoading = true;
                        loadMore();
                        //Toast.makeText(PhuKienActivity.this, "LoadMore()", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.add(null);
                sanPhamAdapter.notifyItemInserted(sanPhamMoiList.size() - 1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.remove(sanPhamMoiList.size() - 1);
                sanPhamAdapter.notifyItemRemoved(sanPhamMoiList.size());
                page = page + 1;
                getData(page);
                sanPhamAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getData(int page) {
        ApiShop.getApiShop.getSanPham(page, loai)
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
                            if (sanPhamAdapter == null) {
                                sanPhamMoiList = sanPhamMoiModel.getResult();
                                sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), sanPhamMoiList);
                                recyclerView.setAdapter(sanPhamAdapter);
                            } else {
                                int pos = sanPhamMoiList.size() - 1;
                                int soLuongAdd = sanPhamMoiModel.getResult().size();
                                for (int i = 0; i < soLuongAdd; i++) {
                                    sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                }
                                sanPhamAdapter.notifyItemRangeChanged(pos, soLuongAdd);
                            }
                        } else {
                            Toast.makeText(getApplication(), "Hết dữ liệu", Toast.LENGTH_SHORT).show();
                            isLoading = true;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("QuanAoActivity", "getData " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("QuanAoActivity", "getData lấy DL ok");
                    }
                });
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

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_pk);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null)
            disposable.dispose();
    }

}