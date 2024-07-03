package com.example.app_qr_code_chinh.QL_cam_sanh;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.Cam;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GD_activity_update extends Fragment {

    private ImageView anhCapNhat;
    private EditText tenCapNhat, nguonGocCapNhat, ngayThuHoachCapNhat, trongLuongCapNhat, moTaCapNhat, ngayTaoCapNhat;
    private Button nutCapNhat;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri uri;
    private String key, qrCode, oldImageURL, imageUrl;
    private double trongLuong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nạp giao diện cho fragment này
        View view = inflater.inflate(R.layout.fragment_g_d_activity_update, container, false);

        // Ánh xạ các thành phần giao diện với các biến trong Java
        anhCapNhat = view.findViewById(R.id.updateImage);
        tenCapNhat = view.findViewById(R.id.detailName);
        nguonGocCapNhat = view.findViewById(R.id.detailOrigin);
        ngayThuHoachCapNhat = view.findViewById(R.id.detailHarvestDate);
        trongLuongCapNhat = view.findViewById(R.id.detailWeight);
        moTaCapNhat = view.findViewById(R.id.detailDesc);
        ngayTaoCapNhat = view.findViewById(R.id.detailCreatedDate);
        nutCapNhat = view.findViewById(R.id.updateButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data != null ? data.getData() : null;
                            if (uri != null) {
                                anhCapNhat.setImageURI(uri);
                            }
                        } else {
                            Toast.makeText(getContext(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Retrieve the arguments
        if (getArguments() != null) {
            String imageUrl = getArguments().getString("ImageUrl");
            String name = getArguments().getString("Name");
            String origin = getArguments().getString("Origin");
            String harvestDate = getArguments().getString("HarvestDate");
            String weight = getArguments().getString("Weight");
            String description = getArguments().getString("Description");
            String createdDate = getArguments().getString("CreatedDate");
            qrCode = getArguments().getString("QrCode");
            key = getArguments().getString("Key");

            // Set the values to the views
            Glide.with(this).load(imageUrl).into(anhCapNhat);
            tenCapNhat.setText(name);
            nguonGocCapNhat.setText(origin);
            ngayThuHoachCapNhat.setText(harvestDate);
            trongLuongCapNhat.setText(weight);
            moTaCapNhat.setText(description);
            ngayTaoCapNhat.setText(createdDate);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Cam").child(key);

        anhCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        nutCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            storageReference = FirebaseStorage.getInstance().getReference().child("Cam").child(imageName);

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
                                    updateData(dialog); // Tiếp tục cập nhật dữ liệu
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
            // Không có ảnh mới, trực tiếp cập nhật dữ liệu
            updateData(null);
        }
    }

    // Phương thức để cập nhật dữ liệu
    public void updateData(AlertDialog dialog) {
        // Lấy các giá trị từ các trường nhập liệu
        String tenSanPham = tenCapNhat.getText().toString().trim();
        String nguonGoc = nguonGocCapNhat.getText().toString().trim();
        String ngayThuHoach = ngayThuHoachCapNhat.getText().toString().trim();
        trongLuong = Double.parseDouble(trongLuongCapNhat.getText().toString());
        String moTa = moTaCapNhat.getText().toString().trim();
        String ngayTao = ngayTaoCapNhat.getText().toString().trim();
        String urlHinhAnh = (uri != null) ? imageUrl : oldImageURL;

        // Tạo đối tượng Cam với các thông tin đã nhập
        Cam camSanPham = new Cam(key, tenSanPham, nguonGoc, ngayThuHoach, trongLuong, moTa, ngayTao, qrCode, urlHinhAnh);

        // Cập nhật dữ liệu trong Firebase Realtime Database
        databaseReference.setValue(camSanPham)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Tùy chọn: Xóa ảnh cũ nếu URI không null
                        if (uri != null && oldImageURL != null) {
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                            reference.delete();
                        }
                        Toast.makeText(getActivity(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        navigateBack(); // Quay lại sau khi cập nhật thành công
                    } else {
                        Toast.makeText(getActivity(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    if (dialog != null) {
                        dialog.dismiss(); // Đóng hộp thoại tiến trình
                    }
                });
    }

    // Phương thức để quay lại màn hình trước đó
    private void navigateBack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); // Loại bỏ fragment khỏi back stack
        } else {
            requireActivity().finish(); // Kết thúc activity nếu không có fragment nào trong back stack
        }
    }
}