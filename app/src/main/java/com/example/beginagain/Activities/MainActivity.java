package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.beginagain.Adapter.LoaiSpAdapter;
import com.example.beginagain.Adapter.SanPhamMoiAdapter;
import com.example.beginagain.Adapter.SanPhamTotAdapter;
import com.example.beginagain.Model.LoaiSp;
import com.example.beginagain.Model.LoaiSpModel;
import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.Model.SanPhamMoiModel;
import com.example.beginagain.Model.User;
import com.example.beginagain.R;
import com.example.beginagain.Retrofit.ApiShop;
import com.example.beginagain.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private ViewFlipper viewFlipper;
    private RecyclerView recyclerViewSPNew;
    private RecyclerView recyclerViewSPBest;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ApiShop apiShop;
    private Disposable disposable;
    private NotificationBadge badge;
    private FrameLayout frameLayout;
    private ImageView imageSearch;
    private List<SanPhamMoi> mangSpMoi;
    private List<SanPhamMoi> mangSpTot;
    private List<LoaiSp> mangLoaiSp;
    //private ListView listViewTrangChinh;
    private TextView tvEmailAddress, tvUseNameCur;
    private LoaiSpAdapter loaiSpAdapter;
    private SanPhamMoiAdapter sanPhamMoiAdapter;
    private SanPhamTotAdapter sanPhamTotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        initView();
        ActionBar();
        if (isConnected(this)) {
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getSpTot();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "Không kết nối Internet, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }

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
    }

    private void getSpTot() {
        apiShop.getApiShop.getSpMoi()
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

    private void getSpMoi() {
        apiShop.getApiShop.getSpMoi()
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

    private void getLoaiSanPham() {
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
                            mangLoaiSp.add(new LoaiSp("Quản Lí", "https://previews.123rf.com/images/illizium/illizium1904/illizium190400043/124063357-project-management-icon-in-flat-style-project-symbol-for-your-web-site-design-logo-app-ui-vector-ill.jpg"));
                            mangLoaiSp.add(new LoaiSp("Thống Kê", "https://thumbs.dreamstime.com/b/growth-business-success-increase-icon-vector-clean-coin-up-arrow-147142774.jpg"));
                            mangLoaiSp.add(new LoaiSp("Đăng xuất", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ35b7r6Hlo7XRTGOOjC-6pyqUwxSgej-0YjDVVZ5NHi6378PWi686DB87MlVK5r77_vDo&usqp=CAU"));
                            loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangLoaiSp);
                            //listViewTrangChinh.setAdapter(loaiSpAdapter);
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

    }

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
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initView() {
        imageSearch = findViewById(R.id.imgseach);
        toolbar = findViewById(R.id.toobartrangchinh);
        viewFlipper = findViewById(R.id.vewlipper);
        //listViewTrangChinh = findViewById(R.id.listviewtrangchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);

        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        tvEmailAddress = headerView.findViewById(R.id.tvEmailAddress);
        tvUseNameCur = headerView.findViewById(R.id.tvUserNameCur);
        tvEmailAddress.setText(Utils.user_current.getEmail());
        tvUseNameCur.setText(Utils.user_current.getUsername());

        recyclerViewSPNew = findViewById(R.id.recycleviewSPNew);
        recyclerViewSPBest = findViewById(R.id.recycleviewSPBest);
        RecyclerView.LayoutManager layoutManagerNew = new GridLayoutManager(this, 2);
        RecyclerView.LayoutManager layoutManagerBest = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSPNew.setLayoutManager(layoutManagerNew);
        recyclerViewSPNew.setHasFixedSize(true);
        recyclerViewSPBest.setLayoutManager(layoutManagerBest);
        recyclerViewSPBest.setHasFixedSize(true);

        mangSpMoi = new ArrayList<>();
        mangLoaiSp = new ArrayList<>();
        mangSpTot = new ArrayList<>();
        if (Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        } else {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gioHang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(gioHang);
            }
        });

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            apiShop.getApiShop.updateToken(Utils.user_current.getId(), s)
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
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }
}