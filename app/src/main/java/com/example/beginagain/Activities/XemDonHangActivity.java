package com.example.beginagain.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beginagain.Adapter.DonHangAdapter;
import com.example.beginagain.Model.DonHang;
import com.example.beginagain.Model.DonHangModel;
import com.example.beginagain.Model.EventBus.DonHangEvent;
import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonHangActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rcvDonHang;
    private DonHang donHang;
    private AlertDialog dialog;
    private int tinhtrang;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don_hang);
        initView();
        initToolbar();
        getOrder();
    }

    private void getOrder() {
        ApiShop.getApiShop.xemDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DonHangModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull DonHangModel donHangModel) {
                        DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                        rcvDonHang.setAdapter(adapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("XemDonHangActivity", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("XemDonHangActivity", "Lấy Đơn hàng thành công!");
                    }
                });
    }

    private void initToolbar() {
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

    private void initView() {
        rcvDonHang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toobar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDonHang.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
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
//        donHang = null;
//        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
//        donHang = null;
//        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event){
        if(event != null){
            //Toast.makeText(this, "Event luôn", Toast.LENGTH_SHORT).show();
            donHang = event.getDonHang();
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_don_hang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btnDongY = view.findViewById(R.id.dongy_dialog);
        TextView tvIdUserDh = view.findViewById(R.id.tvIdUserDh);
        TextView tvDiaChiDh = view.findViewById(R.id.tvDiaChiDh);
        TextView tvSdtDh = view.findViewById(R.id.tvSdtDh);

        tvIdUserDh.setText(donHang.getIdUser()+"");
        tvDiaChiDh.setText(donHang.getDiachi());
        tvSdtDh.setText(donHang.getSodienthoai());

        List<String> list = new ArrayList<>();
        list.add("Đơn hàng đang được xử lí");
        list.add("Đơn hàng đã được chấp nhận");
        list.add("Đơn hàng đã giao cho đơn vị vận chuyển");
        list.add("Đơn hàng đã giao thành công");
        list.add("Đơn hàng đã hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tinhtrang = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capNhatDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    private void capNhatDonHang() {
        ApiShop.getApiShop.updateDonHang(donHang.getId(), tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageModels>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull MessageModels messageModels) {
                        getOrder();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}