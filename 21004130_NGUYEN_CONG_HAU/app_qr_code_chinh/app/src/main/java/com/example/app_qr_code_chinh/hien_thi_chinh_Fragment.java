package com.example.app_qr_code_chinh;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app_qr_code_chinh.databinding.ActivityMainBinding;
import com.example.app_qr_code_chinh.lichsu.GD_lich_su_quet_ma;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.AlertDialog;

public class hien_thi_chinh_Fragment extends Fragment {
    ActivityMainBinding binding;
    private int quyen = 1;
    private String ten_user;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_hien_thi_chinh_, container, false);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Lấy trạng thái đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Kiểm tra trạng thái đăng nhập khi ứng dụng bắt đầu
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Ánh xạ các thành phần trong layout
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        //lấy quyền từ gd_dang nhap
        Bundle bundle = getArguments();
        if (bundle != null) {
            quyen = bundle.getInt("quyen");
            ten_user= bundle.getString("ten");
            saveUserRole(quyen);
            // Sử dụng biến quyen ở đây
        }

        // Thiết lập menu cho BottomNavigationView dựa vào quyền
        // Khai báo biến builder và dialog ở mức độ phạm vi bên ngoài switch case
        AlertDialog.Builder builder;
        AlertDialog dialog;

        switch (quyen) {
            case 0:
                bottomNavigationView.inflateMenu(R.menu.botton_nav_menu_admin);
                replaceFragment(new GD_quan_ly_nguoi_dung());
                break;
            case 1:
                bottomNavigationView.inflateMenu(R.menu.botton_nav_menu_user);
                replaceFragment(new GD_quet_ma_qr());
                break;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // user
            if (itemId == R.id.quet_ma_xml) {
                replaceFragment(new GD_quet_ma_qr());
            } else if (itemId == R.id.lich_su_quet_ma_xml) {
                if (isLoggedIn) {
                    replaceFragment(new GD_lich_su_quet_ma());
                } else {
                    replaceFragment(new GD_yeu_cau_dang_nhap());
                }
            } else if (itemId == R.id.xem_them_xml) {
                if (isLoggedIn) {
                    replaceFragment(new GD_xem_them());
                } else {
                    replaceFragment(new GD_yeu_cau_dang_nhap());
                }
                // admin
            } else if (itemId == R.id.quan_ly_nguoi_dung_xml) {
                replaceFragment(new GD_quan_ly_nguoi_dung());
            } else if (itemId == R.id.quan_ly_thong_tin_xml) {
                replaceFragment(new GD_quan_ly_thong_tin_cam_sanh());
            } else if (itemId == R.id.xem_them_admin_xml) {
                replaceFragment(new GD_xem_them());
            }
            // nha vuon
            return true;
        });

        return view;
    }

    // Phương thức này thay thế fragment hiện tại bằng một fragment mới được chuyển vào.
    // @param fragment Fragment mới sẽ được hiển thị
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // Thay thế fragment trong frame_layout (id được xác định trong file layout) bằng fragment mới
        fragmentTransaction.commit(); // Xác nhận giao dịch và áp dụng thay đổi
    }

    private void showAlertDialog(String role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Người dùng là: " + role)
                .setTitle("Thông báo")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearLoginState() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void saveUserRole(int role) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userRole", role);
        editor.apply();
    }

//    private void checkAndClearLoginState() {
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
//        int userRole = sharedPreferences.getInt("userRole", -1); // Default to -1 if not found
//        if (userRole == 0) { // Assuming 0 represents admin
//            clearLoginState();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        checkAndClearLoginState();
//    }
}