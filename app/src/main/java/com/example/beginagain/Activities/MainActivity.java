package com.example.beginagain.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.beginagain.Adapter.LoaiSpAdapter;
import com.example.beginagain.Adapter.SanPhamMoiAdapter;
import com.example.beginagain.Adapter.SanPhamTotAdapter;
import com.example.beginagain.Adapter.ThongKeDhAdapter;
import com.example.beginagain.Adapter.TkDhTheoThangAdapter;
import com.example.beginagain.Model.DonHang;
import com.example.beginagain.Model.DonHangModel;
import com.example.beginagain.Model.LoaiSp;
import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.Model.ThongKeModel;
import com.example.beginagain.Model.User;
import com.example.beginagain.Model.UserModel;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.example.beginagain.Retrofit.RetrofitService;
import com.example.beginagain.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private TextView tvSoLuongSp, tvDoanhSoThang, tvThoiGian;
    private ListView lvDonHangHomNay, lvDonHangThang;
    private Spinner spinner_thang;
    private LinearLayout linearLayoutCart1, linearLayoutCart2;
    private ViewFlipper viewFlipper;
    private RecyclerView recyclerViewSPNew;
    private RecyclerView recyclerViewSPBest;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ApiShop apiShop;
    private RetrofitService retrofitService;
    private Disposable disposable;
    private NotificationBadge badge;
    private FrameLayout frameLayout;
    private ImageView imageSearch;
    private List<SanPhamMoi> mangSpMoi;
    private List<SanPhamMoi> mangSpTot;
    private List<LoaiSp> mangLoaiSp;
    //private ListView listViewTrangChinh;
    private TextView tvEmailAddress, tvUseNameCur;
    private AlertDialog dialog;
    private LoaiSpAdapter loaiSpAdapter;
    private CircleImageView circleImageView;
    private SanPhamMoiAdapter sanPhamMoiAdapter;
    private SanPhamTotAdapter sanPhamTotAdapter;
    private String currentMonth, currentDay, currentMonthYear;
    private List<DonHang> donHangTodayList, donHangMonthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitService = new RetrofitService();
        apiShop = retrofitService.getRetrofit().create(ApiShop.class);

        Paper.init(this);
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        initView();
        ActionBar();
        if (isConnected(this)) {
//            ActionViewFlipper();
            //getLoaiSanPham();
//            getSpMoi();
//            getSpTot();
            getTongSp();
            getTongTien();
            getDonHangHomNay();
            getDonHangThang(currentMonth);
            catchSpinnerEvent();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "Không kết nối Internet, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }

    }

    private void catchSpinnerEvent() {
        spinner_thang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >= 1){
                    currentMonth = getMonth(i);
                    getDonHangThang(currentMonth);
                    getTongTien();
                    getTongSp();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getTongTien() {
        apiShop.getThongKe4(currentMonth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if (thongKeModel.isSuccess()) {
                            for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                long tongTienSp = thongKeModel.getResult().get(i).getTong();
                                DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");
                                tvDoanhSoThang.setText(decimalFormat.format(tongTienSp));
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu thành công");
                    }
                });
    }

    private void getTongSp() {
        apiShop.getThongKe3(currentMonth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThongKeModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ThongKeModel thongKeModel) {
                        if (thongKeModel.isSuccess()) {
                            for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                long tongSp = thongKeModel.getResult().get(i).getTong();
                                tvSoLuongSp.setText(String.valueOf(tongSp));
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy dữ liệu thành công");
                    }
                });
    }

    private void getDonHangThang(String month) {
        apiShop.xemDonHang2(month)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DonHangModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull DonHangModel donHangModel) {
                        donHangMonthList = donHangModel.getResult();
                        TkDhTheoThangAdapter adapter = new TkDhTheoThangAdapter(getApplicationContext(), donHangMonthList);
                        lvDonHangThang.setAdapter(adapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "Lỗi lấy đơn hàng theo tháng " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy đơn hàng theo tháng thành công!");
                        if (donHangMonthList.size() == 0) {
                            lvDonHangThang.setVisibility(View.GONE);
                            linearLayoutCart2.setVisibility(View.VISIBLE);
                        } else {
                            lvDonHangThang.setVisibility(View.VISIBLE);
                            linearLayoutCart2.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getDonHangHomNay() {
        apiShop.xemDonHang2(currentDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DonHangModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull DonHangModel donHangModel) {
                        donHangTodayList = donHangModel.getResult();
                        ThongKeDhAdapter adapter = new ThongKeDhAdapter(getApplicationContext(), donHangTodayList);
                        lvDonHangHomNay.setAdapter(adapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "Lỗi lấy đơn hàng hôm nay " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Lấy đơn hàng hôm nay thành công!");
                        if (donHangTodayList.size() == 0) {
                            lvDonHangHomNay.setVisibility(View.GONE);
                            linearLayoutCart1.setVisibility(View.VISIBLE);
                        } else {
                            lvDonHangHomNay.setVisibility(View.VISIBLE);
                            linearLayoutCart1.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getEventClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.mnu_trangchu:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case R.id.mnu_quanao:
                        Intent quanao = new Intent(getApplicationContext(), QuanAoActivity.class);
                        startActivity(quanao);
                        break;
                    case R.id.mnu_phukien:
                        Intent phukien = new Intent(getApplicationContext(), PhuKienActivity.class);
                        startActivity(phukien);
                        break;
                    case R.id.mnu_donhang:
                        Intent donhang = new Intent(getApplicationContext(), XemDonHangActivity.class);
                        startActivity(donhang);
                        break;
                    case R.id.mnu_thongtin:
                        Intent thongtin = new Intent(getApplicationContext(), ThongTinActivity.class);
                        startActivity(thongtin);
                        break;
                    case R.id.mnu_quanli:
                        Intent quanli = new Intent(getApplicationContext(), QuanLiActivity.class);
                        startActivity(quanli);
                        break;
                    case R.id.mnu_thongke:
                        Intent thongke = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(thongke);
                        break;
                    case R.id.mnu_dangxuat:
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
                return true;
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_thong_tin, null);
        builder.setView(view);

        AppCompatButton btnXacNhan = view.findViewById(R.id.btndangki);
        ImageView imgClose = view.findViewById(R.id.imgClose);
        EditText edtName = view.findViewById(R.id.username);
        EditText edtEmail = view.findViewById(R.id.email);
        EditText edtSdt = view.findViewById(R.id.mobile);
        EditText edtAddress = view.findViewById(R.id.address);

        edtName.setText(Utils.user_current.getUsername());
        edtEmail.setText(Utils.user_current.getEmail());
        edtSdt.setText(Utils.user_current.getMobile());
        edtAddress.setText(Utils.user_current.getDiachi());

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_name = edtName.getText().toString().trim();
                String str_email = edtEmail.getText().toString().trim();
                String str_sdt = edtSdt.getText().toString().trim();
                String str_address = edtAddress.getText().toString().trim();
                int id = Utils.user_current.getId();

                if (TextUtils.isEmpty(str_name)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_sdt)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập SĐT", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_address)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    apiShop.updateInfo(str_name, str_email, str_sdt, str_address, id)
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
                                    Log.d(TAG, "Loi updateinfo " + e.getMessage());
                                }

                                @Override
                                public void onComplete() {
                                    Utils.user_current.setDiachi(str_address);
                                    Toast.makeText(MainActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    /*
    private void getSpTot() {
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
                            mangSpTot = sanPhamMoiModel.getResult();
                            sanPhamTotAdapter = new SanPhamTotAdapter(getApplicationContext(), mangSpTot);
                            recyclerViewSPBest.setAdapter(sanPhamTotAdapter);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "getSpTot " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "getSpTot Lấy DL xong");
                    }
                });
    }

     */

    /*
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
                            mangSpMoi = sanPhamMoiModel.getResult();
                            sanPhamMoiAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                            recyclerViewSPNew.setAdapter(sanPhamMoiAdapter);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "getSpMoi " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "getSpMoi Lấy DL xong");
                    }
                });

    }

     */

   /* private void getLoaiSanPham() {
        apiShop.getApiShop.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoaiSpModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull LoaiSpModel loaiSpModel) {
                        if (loaiSpModel.isSuccess()) {
                            mangLoaiSp = loaiSpModel.getResult();
                            mangLoaiSp.add(new LoaiSp("Giày", "https://previews.123rf.com/images/illizium/illizium1904/illizium190400043/124063357-project-management-icon-in-flat-style-project-symbol-for-your-web-site-design-logo-app-ui-vector-ill.jpg"));
                            mangLoaiSp.add(new LoaiSp("Nón", "https://thumbs.dreamstime.com/b/growth-business-success-increase-icon-vector-clean-coin-up-arrow-147142774.jpg"));
                            mangLoaiSp.add(new LoaiSp("", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ35b7r6Hlo7XRTGOOjC-6pyqUwxSgej-0YjDVVZ5NHi6378PWi686DB87MlVK5r77_vDo&usqp=CAU"));
                            loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangLoaiSp);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "getLoaiSp " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "getLoaiSp Lấy DL xong");
                    }
                });

    } */

    /*
    private void ActionViewFlipper() {
        List<Integer> mangQuangCao = new ArrayList<>();
        mangQuangCao.add(R.drawable.banner_1);
        mangQuangCao.add(R.drawable.banner_2);
        mangQuangCao.add(R.drawable.banner_3);
        for (int i = 0; i < mangQuangCao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangQuangCao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

     */

    private void ActionBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initView() {
//        imageSearch = findViewById(R.id.imgseach);
        toolbar = findViewById(R.id.toobartrangchinh);
//        viewFlipper = findViewById(R.id.vewlipper);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
//        badge = findViewById(R.id.menu_sl);
//        frameLayout = findViewById(R.id.framegiohang);
        tvSoLuongSp = findViewById(R.id.tvSoLuongSp);
        tvDoanhSoThang = findViewById(R.id.tvDoanhSoThang);
        tvThoiGian = findViewById(R.id.tvThoiGian);
        lvDonHangHomNay = findViewById(R.id.lvDonHangHomNay);
        lvDonHangThang = findViewById(R.id.lvDonHangThang);
        spinner_thang = findViewById(R.id.spinner_thang);
        linearLayoutCart1 = findViewById(R.id.linearLayoutCart);
        linearLayoutCart2 = findViewById(R.id.linearLayoutCart2);

//        Date currentDate = new Date();
//        SimpleDateFormat ft = new SimpleDateFormat("MM/yyyy");
//        SimpleDateFormat ft2 = new SimpleDateFormat("%-MM%");
//        SimpleDateFormat ft3 = new SimpleDateFormat("yyyy-MM-dd");
        this.currentMonthYear = formatDate(0);
        this.currentMonth = formatDate(1);
        this.currentDay = formatDate(2);
        tvThoiGian.setText("Tháng " + currentMonthYear);

        List<String> stringList = new ArrayList<>();
        stringList.add("Chọn tháng");
        for (int i = 1; i <= 12; i++) {
            stringList.add("Tháng " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
        spinner_thang.setAdapter(adapter);

        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        circleImageView = headerView.findViewById(R.id.profile_image);
        tvEmailAddress = headerView.findViewById(R.id.tvEmailAddress);
        tvUseNameCur = headerView.findViewById(R.id.tvUserNameCur);
        tvEmailAddress.setText(Utils.user_current.getEmail());
        tvUseNameCur.setText(Utils.user_current.getUsername());


//        recyclerViewSPNew = findViewById(R.id.recycleviewSPNew);
//        recyclerViewSPBest = findViewById(R.id.recycleviewSPBest);
//        RecyclerView.LayoutManager layoutManagerNew = new GridLayoutManager(this, 2);
//        RecyclerView.LayoutManager layoutManagerBest = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewSPNew.setLayoutManager(layoutManagerNew);
//        recyclerViewSPNew.setHasFixedSize(true);
//        recyclerViewSPBest.setLayoutManager(layoutManagerBest);
//        recyclerViewSPBest.setHasFixedSize(true);

//        mangSpMoi = new ArrayList<>();
//        mangLoaiSp = new ArrayList<>();
//        mangSpTot = new ArrayList<>();
//        if (Utils.manggiohang == null) {
//            Utils.manggiohang = new ArrayList<>();
//        } else {
//            int totalItem = 0;
//            for (int i = 0; i < Utils.manggiohang.size(); i++) {
//                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
//            }
//            badge.setText(String.valueOf(totalItem));
//        }
//
//        frameLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent gioHang = new Intent(getApplicationContext(), GioHangActivity.class);
//                startActivity(gioHang);
//            }
//        });
//
//        imageSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    public String formatDate(int type) {
        Date currentDate = new Date();
        switch (type) {
            case 0:
                SimpleDateFormat ft = new SimpleDateFormat("MM/yyyy");
                return ft.format(currentDate);
            case 1:
                SimpleDateFormat ft2 = new SimpleDateFormat("%-MM%");
                return ft2.format(currentDate);
            case 2:
                SimpleDateFormat ft3 = new SimpleDateFormat("yyyy-MM-dd");
                return ft3.format(currentDate);
        }
        return "";
    }

    public String getMonth(int i) {
        StringBuilder sb = new StringBuilder("%-MM%");
        if (i < 10) {
            String rs = "0" + i;
            sb.replace(2, 4, rs);
        } else sb.replace(2, 4, String.valueOf(i));
        return sb.toString();
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            apiShop.updateToken(Utils.user_current.getId(), s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<MessageModels>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                            disposable = d;
                                        }

                                        @Override
                                        public void onNext(@NonNull MessageModels messageModels) {

                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            Log.d(TAG, e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {
                                            Log.d(TAG, "Cập nhật Token thành công");
                                        }
                                    });
                        }
                    }
                });
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected())) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        int totalItem = 0;
//        for (int i = 0; i < Utils.manggiohang.size(); i++) {
//            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
//        }
//        badge.setText(String.valueOf(totalItem));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }
}