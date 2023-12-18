package com.example.beginagain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beginagain.Model.DonHang;
import com.example.beginagain.Model.Item;
import com.example.beginagain.R;
import com.example.beginagain.Utils.Utils;

import java.util.List;

public class ThongKeDhAdapter extends BaseAdapter {
    private Context context;
    private List<DonHang> donHangList;

    public ThongKeDhAdapter(Context context, List<DonHang> donHangList) {
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
            view = inflater.inflate(R.layout.item_don_hang_tk, viewGroup, false);
        }

        ImageView imgSpDonHang = view.findViewById(R.id.imgSpDonHang);
        TextView tvDhSdt = view.findViewById(R.id.tvDhSdt);
        TextView tvDhDiaChi = view.findViewById(R.id.tvDhDiaChi);
        TextView tvDhGia = view.findViewById(R.id.tvDhGia);
        TextView tvDhSoluong = view.findViewById(R.id.tvDhSoluong);
        TextView tvDhTenSp = view.findViewById(R.id.tvDhTenSp);

        DonHang donHang = donHangList.get(i);
        String tongTien = donHang.getTongtien();
        String sdt = donHang.getSodienthoai();
        String diaChi = donHang.getDiachi();
        if(donHang.getItem() != null){
            Item item = donHang.getItem().get(0);
            if(item.getHinhanh().contains("http")){
                Glide.with(context).load(item.getHinhanh()).into(imgSpDonHang);
            }else{
                String img = Utils.BASE_URL+"images/"+item.getHinhanh();
                Glide.with(context).load(img).into(imgSpDonHang);
            }
            tvDhSoluong.setText("Số lượng: "+item.getSoluong());
            tvDhTenSp.setText(item.getTensp());
        }

        tvDhSdt.setText("SĐT: "+sdt);
        tvDhDiaChi.setText("Địa chỉ: "+diaChi);
        tvDhGia.setText("Tổng tiền: "+tongTien);

        return view;
    }
}
