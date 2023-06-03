package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beginagain.Adapter.SanPhamAdapter;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.Model.SanPhamMoiModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText edtSearch;
    private RecyclerView recyclerView;
    private ApiShop apiShop;
    private Disposable disposable;
    private List<SanPhamMoi> sanPhamMoiList;
    private SanPhamAdapter sanPhamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolbar();
    }

    private void ActionToolbar() {
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

    private void getDataSearch(String s) {
        sanPhamMoiList.clear();
        apiShop.getApiShop.search(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SanPhamMoiModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull SanPhamMoiModel sanPhamMoiModel) {
                        if (sanPhamMoiModel.isSuccess()) {
                            sanPhamMoiList = sanPhamMoiModel.getResult();
                            sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), sanPhamMoiList);
                            recyclerView.setAdapter(sanPhamAdapter);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("SearchActivity", "getDataSearch " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        sanPhamMoiList = new ArrayList<>();
        edtSearch = findViewById(R.id.edtseach);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_seach);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    sanPhamMoiList.clear();
                    sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), sanPhamMoiList);
                    recyclerView.setAdapter(sanPhamAdapter);
                } else {
                    getDataSearch(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null)
            disposable.dispose();
    }
}