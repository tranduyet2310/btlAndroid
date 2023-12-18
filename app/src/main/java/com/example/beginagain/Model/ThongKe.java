package com.example.beginagain.Model;

public class ThongKe {

    private String tensp;
    private long tong;

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public long getTong() {
        return tong;
    }

    public void setTong(long tong) {
        this.tong = tong;
    }

    @Override
    public String toString() {
        return "ThongKe{" +
                "tensp='" + tensp + '\'' +
                ", tong=" + tong +
                '}';
    }
}
