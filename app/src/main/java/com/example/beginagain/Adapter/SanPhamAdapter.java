package com.example.beginagain.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beginagain.Activities.ChiTietActivity;
import com.example.beginagain.Interface.ItemClickListener;
import com.example.beginagain.Model.SanPhamMoi;
import com.example.beginagain.R;
import com.example.beginagain.Utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SanPhamMoi> list;

    private static final int VIEW_TYPE_DATA = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    public SanPhamAdapter(Context context, List<SanPhamMoi> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_san_pham, parent, false);
            return new ItemViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            SanPhamMoi sanPhamMoi = list.get(position);
            itemViewHolder.tenSp.setText(sanPhamMoi.getTensp().trim());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            itemViewHolder.giaSp.setText("Giá: "+decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp()))+ "đ");
            itemViewHolder.moTa.setText(sanPhamMoi.getMota());
            //Glide.with(context).load(sanPhamMoi.getHinhanh()).into(itemViewHolder.hinhAnh);

            if(sanPhamMoi.getHinhanh().contains("http")){
                Glide.with(context).load(sanPhamMoi.getHinhanh())
                        .into(itemViewHolder.hinhAnh);
            }else{
                String img = Utils.BASE_URL+"images/"+sanPhamMoi.getHinhanh();
                Glide.with(context).load(img).into(itemViewHolder.hinhAnh);
            }


            itemViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet", sanPhamMoi);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tenSp, giaSp, moTa;
        private ImageView hinhAnh;
        private ItemClickListener itemClickListener;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tenSp = itemView.findViewById(R.id.itemdt_ten);
            giaSp = itemView.findViewById(R.id.itemdt_gia);
            moTa = itemView.findViewById(R.id.itemdt_mota);
            hinhAnh = itemView.findViewById(R.id.itemdt_image);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
