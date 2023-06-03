package com.example.beginagain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beginagain.Model.LoaiSp;
import com.example.beginagain.R;

import java.util.List;

public class LoaiSpAdapter extends BaseAdapter {

    private Context context;
    private List<LoaiSp> list;

    public LoaiSpAdapter(Context context, List<LoaiSp> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_list_view, null);
            viewHolder.txtTenSp = view.findViewById(R.id.item_tensp);
            viewHolder.imgHinhAnh = view.findViewById(R.id.item_image);
            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.txtTenSp.setText(list.get(i).getTensanpham());
        Glide.with(context).load(list.get(i).getHinhanh()).into(viewHolder.imgHinhAnh);

        return view;
    }

    public class ViewHolder{
        private TextView txtTenSp;
        private ImageView imgHinhAnh;
    }

}
