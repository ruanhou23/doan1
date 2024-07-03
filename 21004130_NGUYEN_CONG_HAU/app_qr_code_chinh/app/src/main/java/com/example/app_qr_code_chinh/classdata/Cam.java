package com.example.app_qr_code_chinh.classdata;

import java.util.Map;

public class Cam {
    private String idcam;
    private String tenLoai;
    private String xuatXu;
    private String ngayThuHoach;
    private double trongLuong;
    private String moTa;
    private String ngayTao;
    private String maQr;
    private String hinhAnhCam;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Cam(String idcam, String tenLoai, String xuatXu, String ngayThuHoach, double trongLuong, String moTa, String ngayTao, String maQr, String hinhAnhCam) {
        this.idcam = idcam;
        this.tenLoai = tenLoai;
        this.xuatXu = xuatXu;
        this.ngayThuHoach = ngayThuHoach;
        this.trongLuong = trongLuong;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
        this.maQr = maQr;
        this.hinhAnhCam = hinhAnhCam;
    }
    // No-argument constructor required for Firebase
    public Cam() {
    }
    public String getIdcam() {
        return idcam;
    }

    public void setIdcam(String idcam) {
        this.idcam = idcam;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public String getNgayThuHoach() {
        return ngayThuHoach;
    }

    public void setNgayThuHoach(String ngayThuHoach) {
        this.ngayThuHoach = ngayThuHoach;
    }

    public double getTrongLuong() {
        return trongLuong;
    }

    public void setTrongLuong(double trongLuong) {
        this.trongLuong = trongLuong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMaQr() {
        return maQr;
    }

    public void setMaQr(String maQr) {
        this.maQr = maQr;
    }

    public String getHinhAnhCam() {
        return hinhAnhCam;
    }

    public void setHinhAnhCam(String hinhAnhCam) {
        this.hinhAnhCam = hinhAnhCam;
    }
}
