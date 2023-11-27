package com.example.beginagain.Utils;

import com.example.beginagain.Model.GioHang;
import com.example.beginagain.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String BASE_URL="http://172.20.10.3:8800/shop/";
    public static final String WEB_URL = "http://172.20.10.3:8800/xinhstore/";

    public static List<GioHang> manggiohang;

    public static List<GioHang> mangmuahang = new ArrayList<>();

    public static User user_current = new User();

    public static String ID_RECEIVED;

    public static final String SENDID = "idsend";

    public static final String RECEIVEDID = "idreceived";

    public static final String DATETIME = "datetime";


}
