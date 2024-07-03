package com.example.app_qr_code_chinh;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GD_dang_ky extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText inputHoten, inputSdt, inputEmail, inputMatkhau;
    private Button signUpButton;
    private Uri uri;
    private int loai_user, trang_thai = 1;
    private String urlHinhAnh;
    private FirebaseDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_dang_ky, container, false);


        // Tìm và gán các view bằng ID của chúng
        inputHoten = view.findViewById(R.id.input_hoten_xml);
        inputSdt = view.findViewById(R.id.input_sdt_xml);
        inputEmail = view.findViewById(R.id.input_email_xml);
        inputMatkhau = view.findViewById(R.id.input_matkhau_xml);
        signUpButton = view.findViewById(R.id.signUpButton);

        // Thiết lập sự kiện khi click vào nút Đăng ký
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(); // Gọi phương thức để đăng ký người dùng
            }
        });

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Cấu hình GoogleSignInOptions để yêu cầu đăng nhập bằng Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Khởi tạo GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kết quả trả về từ việc khởi động Intent từ GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập bằng Google thành công, xác thực với Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Đăng nhập bằng Google thất bại, cập nhật giao diện người dùng phù hợp
                Toast.makeText(getContext(), "Đăng nhập bằng Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, cập nhật giao diện người dùng với thông tin người dùng đã đăng nhập
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Xác thực thành công.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Đăng nhập thất bại, hiển thị thông báo cho người dùng
                            Toast.makeText(getContext(), "Xác thực thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser() {
        // Validate inputs
        String ten_nguoi_dung = inputHoten.getText().toString().trim();
        String sdt_nguoi_dung = inputSdt.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mat_khau_nguoi_dung = inputMatkhau.getText().toString().trim();

        // Kiểm tra xem các trường nhập liệu có trống không
        if (TextUtils.isEmpty(ten_nguoi_dung) || TextUtils.isEmpty(sdt_nguoi_dung) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(mat_khau_nguoi_dung)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Sử dụng ảnh đại diện mặc định từ thư mục
        StorageReference avatarRef = storageRef.child("images/avtarlogin.jpg");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
            urlHinhAnh = uri.toString();
            dialog.dismiss(); // Đóng hộp thoại tiến trình
            // Tiếp tục kiểm tra và đăng ký người dùng
            checkUserExistsAndRegister(email, ten_nguoi_dung, mat_khau_nguoi_dung, urlHinhAnh);
        }).addOnFailureListener(e -> {
            dialog.dismiss(); // Đóng hộp thoại tiến trình khi thất bại
            Toast.makeText(getActivity(), "Lấy ảnh đại diện thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUserExistsAndRegister(String email, String ten_nguoi_dung, String mat_khau_nguoi_dung, String imageUrl) {
        // Kiểm tra xem người dùng đã tồn tại chưa
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            // Người dùng đã tồn tại
                            Toast.makeText(getContext(), "Email đã được sử dụng bởi người dùng khác", Toast.LENGTH_SHORT).show();
                        } else {
                            // Người dùng chưa tồn tại, tiến hành đăng ký
                            performRegistration(email, ten_nguoi_dung, mat_khau_nguoi_dung, imageUrl);
                        }
                    } else {
                        // Lỗi khi kiểm tra người dùng
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra người dùng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Thực hiện đăng ký nếu người dùng chưa tồn tại
    private void performRegistration(String email, String ten_nguoi_dung, String mat_khau_nguoi_dung, String imageUrl) {
        // Tạo ID người dùng duy nhất
        String id_nguoi_dung = generateUserID();

        // Lấy ngày và giờ hiện tại
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String ngay_tao = dateFormatter.format(new Date());
        String ngay_cap_nhat = ngay_tao; // Giả sử ngày cập nhật ban đầu giống với ngày tạo

        // Chuẩn bị dữ liệu người dùng
        NguoiDung nguoiDung = new NguoiDung(id_nguoi_dung, email, ten_nguoi_dung, imageUrl,
                "hoat_dong", mat_khau_nguoi_dung, ngay_tao, ngay_cap_nhat,
                null, null, "1", "gia_tri_nao_do");

        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("NguoiDung");

        // Lưu dữ liệu người dùng vào Firebase
        reference.child(id_nguoi_dung).setValue(nguoiDung).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Xóa các trường nhập liệu và hiển thị thông báo thành công
                inputHoten.setText("");
                inputSdt.setText("");
                inputEmail.setText("");
                inputMatkhau.setText("");
                Toast.makeText(getContext(), "Đã đăng ký thành công", Toast.LENGTH_SHORT).show();
            } else {
                // Xử lý lỗi
                Toast.makeText(getContext(), "Đăng ký không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
