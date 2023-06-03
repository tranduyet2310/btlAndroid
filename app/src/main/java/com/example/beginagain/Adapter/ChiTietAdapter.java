package com.example.beginagain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beginagain.Model.Item;
import com.example.beginagain.R;

import java.util.List;

public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ChiTietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chi_tiet, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtTen.setText(item.getTensp());
        holder.txtSoLuong.setText("Số lượng: "+item.getSoluong());
        Glide.with(context).load(item.getHinhanh()).into(holder.imageChiTiet);
        holder.txtGia.setText("Giá: "+item.getGia());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageChiTiet;
        private TextView txtTen, txtSoLuong, txtGia;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageChiTiet = itemView.findViewById(R.id.item_imgchitiet);
            txtTen = itemView.findViewById(R.id.item_tenspchitiet);
            txtSoLuong = itemView.findViewById(R.id.item_soluongchitiet);
            txtGia = itemView.findViewById(R.id.item_giachitiet);
        }
    }

}
