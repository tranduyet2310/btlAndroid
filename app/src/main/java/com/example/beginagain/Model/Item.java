package com.example.beginagain.Model;

public class Item {

    private int idsp;
    private String tensp;
    private int soluong;
    private String hinhanh;
    private String gia;

    public Item() {
    }

    public int getIdsp() {
        return idsp;
    }

    public void setIdsp(int idsp) {
        this.idsp = idsp;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    @Override
    public String toString() {
        return "Item{" +
                "idsp=" + idsp +
                ", tensp='" + tensp + '\'' +
                ", soluong=" + soluong +
                ", hinhanh='" + hinhanh + '\'' +
                ", gia='" + gia + '\'' +
                '}';
    }
}
