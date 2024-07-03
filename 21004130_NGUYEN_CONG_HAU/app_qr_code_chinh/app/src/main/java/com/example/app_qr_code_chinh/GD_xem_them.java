package com.example.app_qr_code_chinh;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
public class GD_xem_them extends Fragment {

    private Switch darkModeSwitch;
    private int quyen;
    private String ten_user;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_xem_them, container, false);
        textView = view.findViewById(R.id.usernameTextView);
        ////////////////lay thong tin
        // In GD_xem_them fragment's onCreateView or onViewCreated method
        //////////////////////
        TextView logoutTextView = view.findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức xử lý đăng xuất khi TextView được nhấn
                logout();
            }
        });

        // Nhận dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            String tenUser = args.getString("ten");
            textView.setText("xin chao "+tenUser);
        }

        return view;
    }
    // Trong Fragment hiện tại hoặc Fragment khác có chức năng đăng xuất
    private void logout() {
        // Tạo một AlertDialog để hỏi người dùng có muốn đăng xuất hay không
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Người dùng xác nhận đăng xuất

                        // Xóa trạng thái đăng nhập từ SharedPreferences
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();

                        // Chuyển về màn hình đăng nhập
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        GD_dang_nhap loginFragment = new GD_dang_nhap(); // Thay thế bằng Fragment đăng nhập của bạn
                        fragmentTransaction.replace(R.id.container, loginFragment);
                        fragmentTransaction.commit();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
