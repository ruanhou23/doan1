package com.example.app_qr_code_chinh.classdata;

import java.util.Map;

    public class NguoiDung {
        private String ten;
        private String id;
        private String email;
        private String urlHinhAnh;
        private String trangThai;
        private String matKhau;
        private String ngayTao;
        private String ngayCapNhat;
        private Map<String, Boolean> thongBao;
        private Map<String, Boolean> lichSuQuet;
        private String TT;
        private String Tr;

        private String key;
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public NguoiDung() {}
        public NguoiDung(String id, String email, String ten, String urlHinhAnh, String trangThai, String matKhau,
                         String ngayTao, String ngayCapNhat, Map<String, Boolean> thongBao,
                         Map<String, Boolean> lichSuQuet, String TT, String Tr) {
            this.id = id;
            this.email = email;
            this.ten = ten;
            this.urlHinhAnh = urlHinhAnh;
            this.trangThai = trangThai;
            this.matKhau = matKhau;
            this.ngayTao = ngayTao;
            this.ngayCapNhat = ngayCapNhat;
            this.thongBao = thongBao;
            this.lichSuQuet = lichSuQuet;
            this.TT = TT;
            this.Tr = Tr;
        }

        public NguoiDung(String idNguoiDung, String tenNguoiDung, String urlHinhAnh, String s, String matKhauNguoiDung, String ngayTao, String ngayCapNhat, Map<String, Boolean> thongBao, Map<String, Boolean> lichSuQuet, String s1, String giaTriNaoDo) {
        }

        public String getTen() {
            return ten;
        }

        public String getId(String key) {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getUrlHinhAnh() {
            return urlHinhAnh;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public String getMatKhau() {
            return matKhau;
        }

        public String getNgayTao() {
            return ngayTao;
        }

        public String getNgayCapNhat() {
            return ngayCapNhat;
        }

        public Map<String, Boolean> getThongBao() {
            return thongBao;
        }

        public Map<String, Boolean> getLichSuQuet() {
            return lichSuQuet;
        }

        public String getTT() {
            return TT;
        }

        public String getTr() {
            return Tr;
        }

        public void setTen(String ten) {
            this.ten = ten;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setUrlHinhAnh(String urlHinhAnh) {
            this.urlHinhAnh = urlHinhAnh;
        }

        public void setTrangThai(String trangThai) {
            this.trangThai = trangThai;
        }

        public void setMatKhau(String matKhau) {
            this.matKhau = matKhau;
        }

        public void setNgayTao(String ngayTao) {
            this.ngayTao = ngayTao;
        }

        public void setNgayCapNhat(String ngayCapNhat) {
            this.ngayCapNhat = ngayCapNhat;
        }

        public void setThongBao(Map<String, Boolean> thongBao) {
            this.thongBao = thongBao;
        }

        public void setLichSuQuet(Map<String, Boolean> lichSuQuet) {
            this.lichSuQuet = lichSuQuet;
        }

        public void setTT(String TT) {
            this.TT = TT;
        }

        public void setTr(String tr) {
            Tr = tr;
        }
    }
