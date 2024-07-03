package com.example.app_qr_code_chinh.QL_nguoidung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_qr_code_chinh.GD_quan_ly_nguoi_dung;
import com.example.app_qr_code_chinh.R;
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
import java.util.Random;

public class GD_activity_upload_user extends Fragment {
    private EditText inputHoten, inputSdt, inputEmail, inputMatkhau;
    private RadioButton quyenAdmin, quyenNhaVuon, quyenUser, trangThaiHoatDong, trangThaiBiKhoa;
    private Button button;
    private Uri uri;
    private ImageView userloadimg;
    private String ngay_tao, ngay_cap_nhat;
    private int loai_user, trang_thai = 1;
    private String id_nguoi_dung, ten_nguoi_dung, sdt_nguoi_dung, email, mat_khau_nguoi_dung, urlHinhAnh;
    private FirebaseDatabase db;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_d_activity_upload_user, container, false);

        // Tìm các view theo ID của chúng
        inputHoten = view.findViewById(R.id.input_hoten_xml);
        inputSdt = view.findViewById(R.id.input_sdt_xml);
        inputEmail = view.findViewById(R.id.input_email_xml);
        inputMatkhau = view.findViewById(R.id.input_matkhau_xml);
        quyenAdmin = view.findViewById(R.id.quyen_admin_xml);
        quyenUser = view.findViewById(R.id.quyen_user_xml);
        userloadimg = view.findViewById(R.id.uploadImage_user);
        trangThaiHoatDong = view.findViewById(R.id.trangthai_hoatdong_xml);
        trangThaiBiKhoa = view.findViewById(R.id.trangthai_bikhoa_xml);
        button = view.findViewById(R.id.tao_nguoi_dung_xml);

        // Đăng ký ActivityResultLauncher để xử lý kết quả chọn hình ảnh
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                uri = data.getData();
                                userloadimg.setImageURI(uri);
                            } else {
                                Toast.makeText(getActivity(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Thiết lập bộ chọn hình ảnh khi nhấp vào ImageView
        userloadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        setupRadioButtons(); // Thiết lập các nút radio
        setupSubmitButton(); // Thiết lập nút gửi

        return view;
    }

    private void setupRadioButtons() {
        // Thiết lập sự kiện khi nút quyenUser được chọn
        quyenUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                quyenAdmin.setChecked(false);
                loai_user = 1; // Gán giá trị loại người dùng là 1
            }
        });

        // Thiết lập sự kiện khi nút quyenAdmin được chọn
        quyenAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                quyenUser.setChecked(false);
                loai_user = 0; // Gán giá trị loại người dùng là 0
            }
        });


        // Thiết lập sự kiện khi nút trangThaiHoatDong được chọn
        trangThaiHoatDong.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                trangThaiBiKhoa.setChecked(false);
                trang_thai = 1; // Gán giá trị trạng thái hoạt động là 1
            }
        });

        // Thiết lập sự kiện khi nút trangThaiBiKhoa được chọn
        trangThaiBiKhoa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                trangThaiHoatDong.setChecked(false);
                trang_thai = 2; // Gán giá trị trạng thái bị khóa là 2
            }
        });
    }

    private void setupSubmitButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Định dạng ngày tháng
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                id_nguoi_dung = generateUserID(); // Tạo ID người dùng
                ten_nguoi_dung = inputHoten.getText().toString(); // Lấy tên người dùng từ input
                sdt_nguoi_dung = inputSdt.getText().toString(); // Lấy số điện thoại từ input
                email = inputEmail.getText().toString(); // Lấy email từ input
                mat_khau_nguoi_dung = inputMatkhau.getText().toString(); // Lấy mật khẩu từ input

                Date currentDate = new Date(); // Lấy ngày hiện tại
                SimpleDateFormat dateFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.getDefault());
                String formattedDate = dateFormatter.format(currentDate); // Định dạng ngày hiện tại

                ngay_tao = formattedDate; // Gán ngày tạo bằng ngày hiện tại
                ngay_cap_nhat = formattedDate; // Gán ngày cập nhật bằng ngày hiện tại

                Map<String, Boolean> thongBao = new HashMap<>(); // Tạo bản đồ thông báo
                Map<String, Boolean> lichSuQuet = new HashMap<>(); // Tạo bản đồ lịch sử quét

                // Kiểm tra nếu các thông tin không rỗng và đã chọn hình ảnh
                if (!ten_nguoi_dung.isEmpty() && !sdt_nguoi_dung.isEmpty() && !email.isEmpty() && !mat_khau_nguoi_dung.isEmpty() && uri != null) {
                    uploadImageToFirebase(uri); // Tải ảnh lên Firebase
                } else {
                    Toast.makeText(getContext(), "Nhập thông tin sai", Toast.LENGTH_SHORT).show(); // Thông báo nếu nhập thông tin sai
                }
            }
        });
    }

    private void uploadImageToFirebase(Uri uri) {
        // Tạo tham chiếu đến Firebase Storage và đường dẫn lưu trữ hình ảnh người dùng
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("User Images")
                .child(uri.getLastPathSegment());

        // Tạo một AlertDialog để hiển thị tiến trình tải lên
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show(); // Hiển thị hộp thoại tiến trình

        // Tải tập tin lên Firebase Storage
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Lấy URL tải xuống của hình ảnh sau khi tải lên thành công
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                urlHinhAnh = urlImage.toString(); // Chuyển đổi URL thành chuỗi
                saveUserData(); // Lưu dữ liệu người dùng
                dialog.dismiss(); // Đóng hộp thoại tiến trình
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss(); // Đóng hộp thoại tiến trình
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
            }
        });
    }

    private void saveUserData() {
        // Tạo một đối tượng NguoiDung với các thông tin cần thiết
        NguoiDung nguoiDung = new NguoiDung(id_nguoi_dung, email, ten_nguoi_dung, urlHinhAnh,
                trangThaiHoatDong.isChecked() ? "hoat_dong" : "bi_khoa", mat_khau_nguoi_dung, ngay_tao,
                ngay_cap_nhat, new HashMap<>(), new HashMap<>(), String.valueOf(trang_thai), "gia_tri_nao_do");

        // Khởi tạo FirebaseDatabase và tham chiếu đến "NguoiDung"
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("NguoiDung");

        // Lưu đối tượng NguoiDung vào Firebase Realtime Database
        reference.child(id_nguoi_dung).setValue(nguoiDung).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    // Xóa nội dung các trường nhập liệu sau khi cập nhật thành công
                    inputHoten.setText("");
                    inputSdt.setText("");
                    inputEmail.setText("");
                    inputMatkhau.setText("");
                    userloadimg.setImageResource(R.drawable.uploadimg);
                    Toast.makeText(getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show(); // Thông báo thành công
//                    GD_quan_ly_nguoi_dung newFragment = new GD_quan_ly_nguoi_dung();
//                    requireActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, newFragment)
//                            .addToBackStack(null)
//                            .commit();
                } else {
                    Toast.makeText(getContext(), "Cập nhật không thành công", Toast.LENGTH_SHORT).show(); // Thông báo thất bại
                }
            }
        });
    }

    private static String generateUserID() {
        int length = 8;
        String characters = "0123456789";
        StringBuilder idBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            idBuilder.append(randomChar);
        }
        return idBuilder.toString();
    }
}
