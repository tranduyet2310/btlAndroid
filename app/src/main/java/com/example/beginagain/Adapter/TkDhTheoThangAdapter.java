package com.example.beginagain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.beginagain.Model.DonHang;
import com.example.beginagain.R;

import java.util.ArrayList;
import java.util.List;

public class TkDhTheoThangAdapter extends BaseAdapter {

    private Context context;
    private List<DonHang> donHangList;

    public TkDhTheoThangAdapter(Context context, List<DonHang> donHangList) {
        this.context = context;
        this.donHangList = donHangList;
    }

    @Override
    public int getCount() {
        return donHangList.size();
    }

    @Override
    public Object getItem(int i) {
        return donHangList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_tk_dh_theo_thang, viewGroup, false);
        }

        TextView tvMaDh = view.findViewById(R.id.tvMaDh);
        TextView tvDcDh = view.findViewById(R.id.tvDcDh);
        TextView tvSdtNguoiDat = view.findViewById(R.id.tvSdtNguoiDat);
        TextView tvTongTienDh = view.findViewById(R.id.tvTongTienDh);
        TextView tvNgayDat = view.findViewById(R.id.tvNgayDat);

        DonHang donHang = donHangList.get(i);
        tvMaDh.setText(String.valueOf(donHang.getId()));
        tvDcDh.setText("Địa chỉ: "+donHang.getDiachi());
        tvSdtNguoiDat.setText("SĐT: "+donHang.getSodienthoai());
        tvTongTienDh.setText("Tổng tiền: "+donHang.getTongtien());

        List<String> arr = new ArrayList<>();
        arr.add("Đang xử lý");
        arr.add("Đã chấp nhận");
        arr.add("Đã giao cho bên giao hàng");
        arr.add("Giao thành công");
        arr.add("Đã hủy");

        tvNgayDat.setText("Trạng thái: "+arr.get(donHang.getTrangthai()));
        return view;
    }

}
