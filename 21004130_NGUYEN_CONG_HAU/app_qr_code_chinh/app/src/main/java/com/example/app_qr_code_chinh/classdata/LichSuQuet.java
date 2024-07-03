package com.example.app_qr_code_chinh.classdata;

public class LichSuQuet {
    private String idqr;
    private String hinhanh;
    private String moTa;
    private String ngayThuHoach;
    private String tenLoai;
    private Double trongLuong;
    private String xuatXu;
    private String maQr;

    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public LichSuQuet(String idqr, String hinhanh, String moTa, String ngayThuHoach, String tenLoai, Double trongLuong, String xuatXu, String maQr) {
        this.idqr = idqr;
        this.hinhanh = hinhanh;
        this.moTa = moTa;
        this.ngayThuHoach = ngayThuHoach;
        this.tenLoai = tenLoai;
        this.trongLuong = trongLuong;
        this.xuatXu = xuatXu;
        this.maQr = maQr;
    }
    public LichSuQuet(){

    }

    public String getIdqr() {
        return idqr;
    }

    public void setIdqr(String idqr) {
        this.idqr = idqr;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNgayThuHoach() {
        return ngayThuHoach;
    }

    public void setNgayThuHoach(String ngayThuHoach) {
        this.ngayThuHoach = ngayThuHoach;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public Double getTrongLuong() {
        return trongLuong;
    }

    public void setTrongLuong(Double trongLuong) {
        this.trongLuong = trongLuong;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public String getMaQr() {
        return maQr;
    }

    public void setMaQr(String maQr) {
        this.maQr = maQr;
    }
}
