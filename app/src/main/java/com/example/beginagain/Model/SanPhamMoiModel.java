package com.example.beginagain.Model;

import java.util.List;

public class SanPhamMoiModel {

    private boolean success;
    private String message;
    private List<SanPhamMoi> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SanPhamMoi> getResult() {
        return result;
    }

    public void setResult(List<SanPhamMoi> result) {
        this.result = result;
    }
}
