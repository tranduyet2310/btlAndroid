package com.example.beginagain.Model;

public class LoaiSp {

    private int id;
    private String tensanpham;
    private String hinhanh;

    public LoaiSp(String tenSanPham, String hinhAnh) {
        this.tensanpham = tenSanPham;
        this.hinhanh = hinhAnh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }
}
