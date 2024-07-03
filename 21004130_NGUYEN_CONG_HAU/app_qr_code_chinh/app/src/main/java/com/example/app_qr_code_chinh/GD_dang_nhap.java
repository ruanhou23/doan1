package com.example.app_qr_code_chinh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;

public class GD_dang_nhap extends Fragment {

    private EditText editTextTen, editTextPassword;
    private Button bntdangnhap, bntdangky;
    private FirebaseAuth mAuth;
    private int quyen;
    private String ten_user,userPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_dang_nhap, container, false);
        bntdangnhap = view.findViewById(R.id.bnt_dang_nhap_xml);
        editTextTen = view.findViewById(R.id.ten_nguoi_dung_dang_nhap_xml);
        editTextPassword = view.findViewById(R.id.input_nhap_mat_khau_xml);
        bntdangky = view.findViewById(R.id.bnt_dang_ky_xml);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra trạng thái đăng nhập khi ứng dụng bắt đầu
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Người dùng đã đăng nhập, chuyển sang fragment chính
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            hien_thi_chinh_Fragment newFragment = new hien_thi_chinh_Fragment();
            fragmentTransaction.replace(R.id.container, newFragment);
            fragmentTransaction.commit();
        }

        bntdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về màn hình đăng ký
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                GD_dang_ky dangKy = new GD_dang_ky(); // Thay thế bằng Fragment đăng ký của bạn
                fragmentTransaction.replace(R.id.container, dangKy);
                fragmentTransaction.commit();
            }
        });

        bntdangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ten = editTextTen.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Kiểm tra xem tên người dùng và mật khẩu có rỗng không
                if (ten.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("NguoiDung");
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isCredentialValid = false;

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String username = userSnapshot.child("email").getValue(String.class);
                            userPassword = userSnapshot.child("matKhau").getValue(String.class);
                            ten_user = userSnapshot.child("ten").getValue(String.class);
                            String loai_user_str = userSnapshot.child("tt").getValue(String.class);
                            int loai_user = Integer.parseInt(loai_user_str);

                            try {
                                loai_user = Integer.parseInt(loai_user_str);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Xử lý lỗi nếu giá trị không thể chuyển đổi thành số nguyên
                            }

                            if (loai_user == 0) {
                                quyen = 0;
                            } else if (loai_user == 1) {
                                quyen = 1;
                            } else if (loai_user == 2) {
                                quyen = 2;
                            }

                            // Kiểm tra xem username và password có tồn tại không
                            if (username != null && userPassword != null && username.equals(ten) && userPassword.equals(password)) {
                                isCredentialValid = true;
                                break; // Kết thúc vòng lặp nếu tìm thấy tên đăng nhập và mật khẩu khớp
                            }
                        }

                        if (isCredentialValid) {
                            // Lưu trạng thái đăng nhập
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

// Tên đăng nhập và mật khẩu đúng, thực hiện chuyển đổi fragment
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

// Tạo Bundle và truyền dữ liệu
                            Bundle bundle = new Bundle();
                            bundle.putInt("quyen", quyen);
                            bundle.putString("ten", ten_user);

// Tạo và thiết lập arguments cho newFragment
                            hien_thi_chinh_Fragment newFragment = new hien_thi_chinh_Fragment();
                            newFragment.setArguments(bundle);

// Tạo và thiết lập arguments cho fragment (nếu cần thiết)
                            Bundle arg = new Bundle();
                            arg.putInt("quyen", quyen);
                            arg.putString("ten", ten_user);
                            // Tạo một instance của Fragment
                            GD_xem_them fragment = new GD_xem_them();

// Gán Bundle cho Fragment
                            fragment.setArguments(arg);
// Thực hiện thay thế fragment
                            fragmentTransaction.replace(R.id.container, newFragment);
                            fragmentTransaction.addToBackStack(null);

// Xóa ngăn xếp phía sau
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

// Commit transaction
                            fragmentTransaction.commit();
                        } else {
                            // Hiển thị thông báo khi tên đăng nhập hoặc mật khẩu không đúng
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Tên đăng nhập hoặc mật khẩu không đúng.")
                                    .setTitle("Lỗi đăng nhập")
                                    .setPositiveButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                        Toast.makeText(getActivity(), "Đã xảy ra lỗi. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}