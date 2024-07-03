package com.example.app_qr_code_chinh;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;


public class MainActivity extends FragmentActivity  implements GD_yeu_cau_dang_nhap.OnLoginButtonClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hien_thi_trang_chinh();
        //them();
    }
    // Handle login button click event

    public void onLoginButtonClick() {
        // Thay thế fragment đăng nhập bằng fragment mới
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GD_dang_nhap newFragment = new GD_dang_nhap(); // Tạo một instance của fragment GD_dang_nhap
        fragmentTransaction.replace(R.id.container, newFragment); // Thay thế fragment đăng nhập bằng GD_dang_nhap
        fragmentTransaction.addToBackStack(null); // Optional, cho phép người dùng quay lại fragment trước đó
        fragmentTransaction.commit();
    }
    private void hien_thi_trang_chinh(){
        // Thay thế fragment đăng nhập bằng fragment mới
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hien_thi_chinh_Fragment newFragment = new hien_thi_chinh_Fragment(); // Tạo một instance của fragment GD_dang_nhap
        fragmentTransaction.replace(R.id.container, newFragment); // Thay thế fragment đăng nhập bằng GD_dang_nhap
        fragmentTransaction.addToBackStack(null); // Optional, cho phép người dùng quay lại fragment trước đó
        fragmentTransaction.commit();
    }
    private void them(){
        // Thay thế fragment đăng nhập bằng fragment mới
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GD_quan_ly_nguoi_dung newFragment = new GD_quan_ly_nguoi_dung(); // Tạo một instance của fragment GD_dang_nhap
        fragmentTransaction.replace(R.id.container, newFragment); // Thay thế fragment đăng nhập bằng GD_dang_nhap
        fragmentTransaction.addToBackStack(null); // Optional, cho phép người dùng quay lại fragment trước đó
        fragmentTransaction.commit();
    }
}
