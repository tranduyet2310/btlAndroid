package com.example.beginagain.Model.EventBus;

import com.example.beginagain.Model.DonHang;

public class DonHangEvent {

    private DonHang donHang;

    public DonHangEvent(DonHang donHang) {
        this.donHang = donHang;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }
}
