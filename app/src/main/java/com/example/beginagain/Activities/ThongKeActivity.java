package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.beginagain.Model.ThongKe;
import com.example.beginagain.Model.ThongKeModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.example.beginagain.Retrofit.RetrofitService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeActivity extends AppCompatActivity {

    public static final String TAG = "ThongKeActivity";
    private Toolbar toolbar;
    private BarChart barChartDonHang;
    private PieChart pieChartSanPham;
    private TableLayout tableLayout;
    private TextView tvTongDonHang, tvDonHangMoi, tvDonHangXuLy, tvDonHangGiao,tvDonHangBiHuy, tvDoanhThu;
    private Disposable disposable;
    private long tongDh, tongDhMoi, tongDhXuLy, tongDhGiao, tongDhVanChuyen, tongDhBiHuy;
    private List<BarEntry> listDataDh = new ArrayList<>();
    private ApiShop apiShop;
    private RetrofitService retrofitService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        retrofitService = new RetrofitService();
        apiShop = retrofitService.getRetrofit().create(ApiShop.class);
        initView();
        ActionToolbar();
        // Thong ke don hang
        getDataDonHang();
        getDonHangMoi();
        getDonHangXuLy();
        getDonHangDaGiao();
        getDonHangVanChuyen();
        getDonHangBiHuy();
        // Ve bieu do minh hoa
        getChartDonHang();
        getDataChart();
        // Tao bang thong ke san pham
        createTableSp();
    }

    private void getDonHangBiHuy() {
        apiShop.getThongKe2(4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                tongDhBiHuy = thongKeModel.getResult().get(i).getTong();
                                tvDonHangBiHuy.setText(""+tongDhBiHuy);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng bị hủy xong");
                    }
                });
    }

    private void createTableSp() {
        TableRow headerRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 10);
        layoutParams.setMargins(15, 5, 15, 5);
        headerRow.setLayoutParams(layoutParams);

        TableRow.LayoutParams rowFirstParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
        rowFirstParams.setMargins(15,2,0,2);

        TableRow.LayoutParams rowSecondParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
        rowSecondParams.setMargins(0,2,15,2);

        TextView headerName = new TextView(this);
        headerName.setText("Tên sản phẩm");
        headerName.setBackgroundColor(Color.parseColor("#B8D9FA"));
        headerName.setGravity(Gravity.CENTER);
        headerName.setTypeface(null, Typeface.BOLD);
        headerName.setLayoutParams(rowFirstParams);
        headerRow.addView(headerName);

        TextView headerQuantity = new TextView(this);
        headerQuantity.setText("Số lượng");
        headerQuantity.setBackgroundColor(Color.parseColor("#E9EAEC"));
        headerQuantity.setGravity(Gravity.CENTER);
        headerQuantity.setTypeface(null, Typeface.BOLD);
        headerQuantity.setLayoutParams(rowSecondParams);
        headerRow.addView(headerQuantity);

        tableLayout.addView(headerRow);

        apiShop.getThongKe3()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            List<ThongKe> productList = thongKeModel.getResult();
                            for (ThongKe product : productList){
                                TableRow dataRow = new TableRow(ThongKeActivity.this);
                                dataRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 10));

                                TableRow.LayoutParams rowFirstParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
                                rowFirstParams.setMargins(15,2,0,2);

                                TableRow.LayoutParams rowSecondParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
                                rowSecondParams.setMargins(0,2,15,2);

                                TextView name = new TextView(ThongKeActivity.this);
                                name.setText(product.getTensp());
                                name.setBackgroundColor(Color.parseColor("#B8D9FA"));
                                name.setGravity(Gravity.CENTER);
                                name.setLayoutParams(rowFirstParams);
                                dataRow.addView(name);

                                TextView quantity = new TextView(ThongKeActivity.this);
                                quantity.setText(String.valueOf(product.getTong()));
                                quantity.setBackgroundColor(Color.parseColor("#E9EAEC"));
                                quantity.setGravity(Gravity.CENTER);
                                quantity.setLayoutParams(rowSecondParams);
                                dataRow.addView(quantity);

                                tableLayout.addView(dataRow);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu số lượng sản phẩm xong!");
                    }
                });

    }

    private void getChartDonHang() {
        BarDataSet barDataSet = new BarDataSet(listDataDh, "Đơn hàng");
        BarData barData = new BarData(barDataSet);
        barChartDonHang.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);
        barChartDonHang.animateXY(2000, 2000);
        barChartDonHang.getDescription().setText("Thống kê đơn hàng");
        barChartDonHang.getDescription().setEnabled(false);
    }

    private void getDonHangDaGiao() {
        apiShop.getThongKe2(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                tongDhGiao = thongKeModel.getResult().get(i).getTong();
                                tvDonHangGiao.setText(""+tongDhGiao);
                                float donhanggiao = (float) tongDhGiao;
                                listDataDh.add(new BarEntry(4f, donhanggiao));
                                getChartDonHang();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng đã giao xong");
                    }
                });

    }

    private void getDonHangVanChuyen() {
        apiShop.getThongKe2(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                tongDhVanChuyen = thongKeModel.getResult().get(i).getTong();
                                float donhanggiao = (float) tongDhVanChuyen;
                                listDataDh.add(new BarEntry(3f, donhanggiao));
                                getChartDonHang();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng vận chuyển xong");
                    }
                });

    }

    private void getDonHangXuLy() {
        apiShop.getThongKe2(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                tongDhXuLy = thongKeModel.getResult().get(i).getTong();
                                tvDonHangXuLy.setText(""+tongDhXuLy);
                                float donhangxuly = (float) tongDhXuLy;
                                listDataDh.add(new BarEntry(2f, donhangxuly));
                                getChartDonHang();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng đã xử lý xong");
                    }
                });
    }

    private void getDonHangMoi() {
        apiShop.getThongKe2(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                tongDhMoi = thongKeModel.getResult().get(i).getTong();
                                tvDonHangMoi.setText(""+tongDhMoi);
                                float donhangmoi = (float) tongDhMoi;
                                listDataDh.add(new BarEntry(1f, donhangmoi));
                                getChartDonHang();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng mới xong");
                    }
                });
    }

    private void getDataDonHang() {
        apiShop.getThongKe1()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if(thongKeModel.isSuccess()){
                            for (int i=0; i < thongKeModel.getResult().size(); i++){
                                String sodon = thongKeModel.getResult().get(i).getTensp();
                                tongDh = Long.parseLong(sodon);
                                tvTongDonHang.setText(sodon);

                                long tong = thongKeModel.getResult().get(i).getTong();
                                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                                String doanhThu = decimalFormat.format(tong);
                                tvDoanhThu.setText(doanhThu);

                                float donhang = (float) tongDh;
                                listDataDh.add(new BarEntry(5f, donhang));
                                getChartDonHang();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu đơn hàng xong");
                    }
                });
    }

    private void getDataChart() {
        List<PieEntry> listData = new ArrayList<>();
        apiShop.getThongKe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if (thongKeModel.isSuccess()){
                            for(int i=0; i< thongKeModel.getResult().size(); i++){
                                String tensp = thongKeModel.getResult().get(i).getTensp();
                                long tong = thongKeModel.getResult().get(i).getTong();
                                listData.add(new PieEntry(tong, tensp));
                            }
                            PieDataSet pieDataSet = new PieDataSet(listData, "Thống kê");
                            PieData pieData = new PieData();
                            pieData.setDataSet(pieDataSet);
                            pieData.setValueTextSize(1f);
                            pieData.setValueFormatter(new PercentFormatter(pieChartSanPham));
                            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            pieChartSanPham.setData(pieData);
                            pieChartSanPham.setUsePercentValues(true);
                            pieChartSanPham.animateXY(2000,2000);
                            pieChartSanPham.getDescription().setEnabled(false);
                            pieChartSanPham.invalidate();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu sản phẩm xong");
                    }
                });
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

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        barChartDonHang = findViewById(R.id.barchartDonHang);
        pieChartSanPham = findViewById(R.id.piechart);
        tableLayout = findViewById(R.id.tableLayout);
        tvTongDonHang = findViewById(R.id.tvTongDonHang);
        tvDonHangMoi = findViewById(R.id.tvDonHangMoi);
        tvDonHangXuLy = findViewById(R.id.tvDonHangXuLy);
        tvDonHangGiao = findViewById(R.id.tvDonHangDaGiao);
        tvDoanhThu = findViewById(R.id.tvDoanhThuDuKien);
        tvDonHangBiHuy = findViewById(R.id.tvDonHangBiHuy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null)
            disposable.dispose();
    }
}