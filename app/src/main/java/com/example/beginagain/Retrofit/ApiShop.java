package com.example.beginagain.Retrofit;

import com.example.beginagain.Model.DonHangModel;
import com.example.beginagain.Model.LoaiSpModel;
import com.example.beginagain.Model.MessageModels;
import com.example.beginagain.Model.SanPhamMoiModel;
import com.example.beginagain.Model.ThongKeModel;
import com.example.beginagain.Model.UserModel;
import com.example.beginagain.Utils.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiShop {

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor);

    ApiShop getApiShop = new Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okBuilder.build())
            .build()
            .create(ApiShop.class);

    @GET("thongke.php")
    Observable<ThongKeModel> getThongKe();

    @GET("thongke1.php")
    Observable<ThongKeModel> getThongKe1();

    @POST("thongke2.php")
    @FormUrlEncoded
    Observable<ThongKeModel> getThongKe2(
            @Field("loai") int trangthai
    );

    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );

    @POST("insertsp.php")
    @FormUrlEncoded
    Observable<MessageModels> insertSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int id
    );

    @POST("xoa.php")
    @FormUrlEncoded
    Observable<MessageModels> xoaSanPham(
            @Field("id") int id
    );

    @POST("updatesp.php")
    @FormUrlEncoded
    Observable<MessageModels> updateSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int idloai,
            @Field("id") int id
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("username") String username,
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );

    @POST("updatedonhang.php")
    @FormUrlEncoded
    Observable<MessageModels> updateDonHang(
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );

    @Multipart
    @POST("upload.php")
    Call<MessageModels> uploadFile(@Part MultipartBody.Part file);

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModels> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("updateinfo.php")
    @FormUrlEncoded
    Observable<UserModel> updateInfo(
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("diachi") String diachi,
            @Field("id") int id
    );

}
