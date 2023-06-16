package com.example.beginagain.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beginagain.Activities.ChiTietActivity;
import com.example.beginagain.Interface.ItemClickListener;
import com.example.beginagain.Model.EventBus.SuaXoaEvent;
import com.example.beginagain.Model.GioHang;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.R;
import com.example.beginagain.Utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamTotAdapter extends RecyclerView.Adapter<SanPhamTotAdapter.ItemViewHolder> {

    private Context context;
    private List<SanPhamMoi> list;

    public SanPhamTotAdapter(Context context, List<SanPhamMoi> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_tot, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = list.get(position);
        holder.txtTenSp.setText(sanPhamMoi.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGiaSp.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp())) + "đ");
        if (sanPhamMoi.getHinhanh().contains("http")) {
            Glide.with(context).load(sanPhamMoi.getHinhanh())
                    .into(holder.imgHinhAnh);
        } else {
            String img = Utils.BASE_URL + "images/" + sanPhamMoi.getHinhanh();
            Glide.with(context).load(img).into(holder.imgHinhAnh);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (!isLongClick) {
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet", sanPhamMoi);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    EventBus.getDefault().postSticky(new SuaXoaEvent(sanPhamMoi));
                }
            }
        });

        holder.btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.manggiohang.size() > 0) {
                    boolean flag = false;
                    int soLuong = 1;
                    for (int i = 0; i < Utils.manggiohang.size(); i++) {
                        if (Utils.manggiohang.get(i).getIdsp() == sanPhamMoi.getId()) {
                            Utils.manggiohang.get(i).setSoluong(soLuong + Utils.manggiohang.get(i).getSoluong());
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        long gia = Long.parseLong(sanPhamMoi.getGiasp());
                        GioHang gioHang = new GioHang();
                        gioHang.setGiasp(gia);
                        gioHang.setSoluong(soLuong);
                        gioHang.setIdsp(sanPhamMoi.getId());
                        gioHang.setTensp(sanPhamMoi.getTensp());
                        gioHang.setHinhanh(sanPhamMoi.getHinhanh());
                        Utils.manggiohang.add(gioHang);
                    }
                } else {
                    int soLuong = 1;
                    long gia = Long.parseLong(sanPhamMoi.getGiasp());
                    GioHang gioHang = new GioHang();
                    gioHang.setGiasp(gia);
                    gioHang.setSoluong(soLuong);
                    gioHang.setIdsp(sanPhamMoi.getId());
                    gioHang.setTensp(sanPhamMoi.getTensp());
                    gioHang.setHinhanh(sanPhamMoi.getHinhanh());
                    Utils.manggiohang.add(gioHang);
                }
                int totalItem = 0;
                for (int i = 0; i < Utils.manggiohang.size(); i++) {
                    totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
                }
                Toast.makeText(context, "Đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtGiaSp, txtTenSp;
        private ImageView imgHinhAnh;
        private AppCompatButton btnThem;
        private ItemClickListener itemClickListener;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenSp = itemView.findViewById(R.id.itemsp_ten);
            txtGiaSp = itemView.findViewById(R.id.itemsp_gia);
            imgHinhAnh = itemView.findViewById(R.id.itemsp_image);
            btnThem = itemView.findViewById(R.id.btn_add_to_cart);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }

    }

}
