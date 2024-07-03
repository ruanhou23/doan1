package com.example.app_qr_code_chinh.QL_nguoidung;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.DataClass;
import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.os.Bundle;

public class GD_activity_update_user extends Fragment {

    private EditText inputHoten, inputEmail, inputMatkhau, iduser;
    private RadioButton quyenAdmin, quyenNhaVuon, quyenUser;
    private ImageView updateImage;
    private RadioButton trangThaiHoatDong, trangThaiBiKhoa;
    private Button button;
    private Uri uri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String imageUrl, oldImageURL, key, ngay_tao, ngay_cap_nhat,tt;
    private String trang_thai;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_d_activity_update_user, container, false);

        iduser = view.findViewById(R.id.id_nguoidung_xml);
        inputHoten = view.findViewById(R.id.input_hoten_xml);
        inputEmail = view.findViewById(R.id.input_email_xml);
        inputMatkhau = view.findViewById(R.id.input_matkhau_xml);
        quyenAdmin = view.findViewById(R.id.quyen_admin_xml);
        quyenUser = view.findViewById(R.id.quyen_user_xml);
        updateImage = view.findViewById(R.id.uploadImage_user);
        trangThaiHoatDong = view.findViewById(R.id.trangthai_hoatdong_xml);
        trangThaiBiKhoa = view.findViewById(R.id.trangthai_bikhoa_xml);
        button = view.findViewById(R.id.tao_nguoi_dung_xml);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data != null ? data.getData() : null;
                            if (uri != null) {
                                updateImage.setImageURI(uri);
                            }
                        } else {
                            Toast.makeText(getContext(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        // Retrieve the arguments
        if (getArguments() != null) {
            imageUrl = getArguments().getString("anh");
            String name = getArguments().getString("Name");
            String status = getArguments().getString("Status");
            String email = getArguments().getString("Email");
            String password = getArguments().getString("Password");
            tt = getArguments().getString("TT");
            key = getArguments().getString("key");

            // Set the values to the views
            Glide.with(this).load(imageUrl).into(updateImage);
            inputHoten.setText(name);
            inputEmail.setText(email);
            inputMatkhau.setText(password);
            iduser.setText(key);
            // Assuming tt indicates some status we need to set
            if (status.equals("active")) {
                trangThaiHoatDong.setChecked(true);
            } else {
                trangThaiBiKhoa.setChecked(true);
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        return view;
    }

    // Phương thức để lưu dữ liệu
    public void saveData() {
        if (uri != null) {
            // Tải ảnh lên Firebase Storage
            String imageName = uri.getLastPathSegment();
            storageReference = FirebaseStorage.getInstance().getReference().child("NguoiDung").child(imageName);

            // Tạo và hiển thị một hộp thoại tiến trình không thể hủy bỏ
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            // Tải tệp lên Firebase Storage
            storageReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Ảnh đã tải lên thành công, lấy URL tải xuống
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    imageUrl = uri.toString();
                                    updateData(dialog); // Tiếp tục cập nhật dữ liệu người dùng
                                })
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Lấy URL tải xuống thất bại.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Không có ảnh mới, trực tiếp cập nhật dữ liệu người dùng
            updateData(null);
        }
    }

    // Phương thức để cập nhật dữ liệu người dùng
    public void updateData(AlertDialog dialog) {
        // Tạo đối tượng dữ liệu người dùng
        String ten_nguoi_dung = inputHoten.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mat_khau_nguoi_dung = inputMatkhau.getText().toString().trim();
        String trang_thai = trangThaiHoatDong.isChecked() ? "hoat_dong" : "bi_khoa";
        String id_nguoi_dung = key; // Giả định key là id_nguoi_dung
        String urlHinhAnh = (uri != null) ? imageUrl : oldImageURL;
        int hoatdong = trang_thai.equals("hoat_dong") ? 1 : 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String ngay_tao = dateFormat.format(new Date()); // Giả định ngày tạo là hiện tại
        String ngay_cap_nhat = dateFormat.format(new Date()); // Giả định ngày cập nhật là hiện tại

        // Tạo đối tượng NguoiDung với các thông tin đã nhập
        NguoiDung nguoiDung = new NguoiDung(id_nguoi_dung, email, ten_nguoi_dung, urlHinhAnh,
                trang_thai, mat_khau_nguoi_dung, ngay_tao, ngay_cap_nhat,
                new HashMap<>(), new HashMap<>(), tt, "gia_tri_nao_do");

        // Cập nhật dữ liệu người dùng trong Firebase Realtime Database
        databaseReference.setValue(nguoiDung)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật thành công
                        if (uri != null && !TextUtils.isEmpty(oldImageURL)) {
                            // Xóa ảnh cũ nếu có
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                            reference.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Xóa thành công ảnh cũ
                                        Toast.makeText(getActivity(), "Đã cập nhật và xóa ảnh cũ", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xóa ảnh cũ thất bại
                                        Toast.makeText(getActivity(), "Cập nhật thành công nhưng xóa ảnh cũ thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Không có ảnh cũ để xóa
                            Toast.makeText(getActivity(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Cập nhật thất bại
                        Toast.makeText(getActivity(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi cập nhật dữ liệu
                    Toast.makeText(getActivity(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    // Đóng hộp thoại tiến trình dù thành công hay thất bại
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                });
    }
}