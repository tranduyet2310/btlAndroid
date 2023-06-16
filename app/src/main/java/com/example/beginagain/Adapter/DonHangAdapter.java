package com.example.beginagain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beginagain.Interface.ItemClickListener;
import com.example.beginagain.Model.DonHang;
import com.example.beginagain.Model.EventBus.DonHangEvent;
import com.example.beginagain.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.ItemViewHoler> {

    private Context context;
    private List<DonHang> listDonHang;

    public DonHangAdapter(Context context, List<DonHang> listDonHang) {
        this.context = context;
        this.listDonHang = listDonHang;
    }

    @NonNull
    @Override
    public ItemViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_don_hang, parent, false);
        return new ItemViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHoler holder, int position) {
        DonHang donHang = listDonHang.get(position);
        holder.txtDonHang.setText("Đơn hàng: " + donHang.getId());
        holder.trangthai.setText(trangThaiDon((donHang.getTrangthai())));

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rcvChiTiet.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context, donHang.getItem());
        holder.rcvChiTiet.setLayoutManager(layoutManager);
        holder.rcvChiTiet.setAdapter(chiTietAdapter);
        holder.rcvChiTiet.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        holder.setListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(isLongClick){
                    //EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                    EventBus.getDefault().post(new DonHangEvent(donHang));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDonHang.size();
    }

    public class ItemViewHoler extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView txtDonHang, trangthai;
        RecyclerView rcvChiTiet;
        private ItemClickListener listener;

        public ItemViewHoler(@NonNull View itemView) {
            super(itemView);
            txtDonHang = itemView.findViewById(R.id.iddonhang);
            trangthai = itemView.findViewById(R.id.tinhtrang);
            rcvChiTiet = itemView.findViewById(R.id.recycleview_chitiet);
            itemView.setOnLongClickListener(this);
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onClick(view, getAdapterPosition(), true);
            return false;
        }
    }

    private String trangThaiDon(int status) {
        String result = "";
        switch (status) {
            case 0:
                result = "Đơn hàng đang được xử lý";
                break;
            case 1:
                result = "Đơn hàng đã được chấp nhận";
                break;
            case 2:
                result = "Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result = "Đơn hàng đã giao thành công";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;
        }
        return result;
    }


}
