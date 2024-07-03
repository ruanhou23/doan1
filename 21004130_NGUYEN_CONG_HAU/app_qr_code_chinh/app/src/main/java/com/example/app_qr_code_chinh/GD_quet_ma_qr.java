package com.example.app_qr_code_chinh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.camera.view.PreviewView;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.classdata.LichSuQuet;
import com.example.app_qr_code_chinh.lichsu.GD_lich_su_quet_ma;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

import com.google.zxing.NotFoundException;

import com.google.zxing.FormatException;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.app_qr_code_chinh.classdata.AESEncryption;
import com.example.app_qr_code_chinh.classdata.DESUtil;

public class GD_quet_ma_qr extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Toolbar toolbar;
    DatabaseReference databaseReference,databaseReference2; // Tham chiếu tới Firebase Database
    private PrivateKey privateKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_d_quet_ma_qr, container, false);


        // Kiểm tra và yêu cầu quyền truy cập camera
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
        // Lấy BottomNavigationView từ view và thiết lập các hành động nếu cần
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.menu2);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.open_from_gallery) {
                // Xử lý sự kiện khi người dùng chọn mục "open_from_gallery"
                openGallery();
                return true;
            } else if (itemId == R.id.flash_toggle) {
                toggleFlash();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        });
        setHasOptionsMenu(true); // Báo cho hệ thống rằng fragment này có một menu

        return view;
    }
/////////////////////////////////////
    // Gọi hàm này khi bạn muốn mở thư viện ảnh để chọn một ảnh
    // Gọi hàm này khi bạn muốn xử lý kết quả từ việc chọn ảnh từ thư viện
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                scanQRCode(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm quét mã QR từ ảnh Bitmap
    private void scanQRCode(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(requireContext(), "Bitmap is null", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);

            // Khi quét thành công, 'result' chứa thông tin mã QR
            String qrText = result.getText();

            // Giải mã dữ liệu
            String decryptedTextDES;
            try {
                decryptedTextDES = DESUtil.decryptDES(qrText);
            } catch (Exception e) {
                //Toast.makeText(requireContext(), "Failed to decrypt DES", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            String decryptedText;
            try {
                decryptedText = AESEncryption.decrypt(decryptedTextDES);
            } catch (Exception e) {
                //Toast.makeText(requireContext(), "Failed to decrypt AES", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            String expectedType = "type_cam_sanh";

            String[] parts = decryptedText.split(":");

            if (parts.length == 2) {
                String type = parts[0];
                String id = parts[1];

                // Kiểm tra nếu type khớp với expectedType
                if (type.equals(expectedType)) {
                    kiemTraIdTonTai(id);
                } else {
                    // Tạo một Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Thông tin mã QR");
                    builder.setMessage("Thông tin sai!");

                    // Thêm nút "Đóng" cho Dialog
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss(); // Đóng Dialog khi nút "Đóng" được nhấn
                        }
                    });

                    // Hiển thị Dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } else {
                Toast.makeText(requireContext(), "Chuỗi không đúng định dạng", Toast.LENGTH_LONG).show();
                Log.e("SplitError", "Chuỗi không đúng định dạng");
            }

        } catch (NotFoundException | ChecksumException | FormatException e) {
            Toast.makeText(requireContext(), "QR Code not found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "An unexpected error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    /////////////////////////////////
    public void kiemTraIdTonTai(String id) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Cam");

        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem dữ liệu có tồn tại hay không
                if (dataSnapshot.exists()) {
                    try {
                        String maqr = dataSnapshot.child("idcam").getValue(String.class);
                        String hinhanh = dataSnapshot.child("hinhAnhCam").getValue(String.class);
                        String mo_ta = dataSnapshot.child("moTa").getValue(String.class);
                        String ngaythuhoach = dataSnapshot.child("ngayThuHoach").getValue(String.class);
                        String tenloai = dataSnapshot.child("tenLoai").getValue(String.class);
                        Double trong_luong = dataSnapshot.child("trongLuong").getValue(Double.class);
                        String xuat_xu = dataSnapshot.child("xuatXu").getValue(String.class);
                        String maQr = dataSnapshot.child("maQr").getValue(String.class);


                        // So sánh id lấy được từ QR với id từ Firebase
                        if (id.equals(maqr)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("idcam", maqr);
                            bundle.putString("hinhanh", hinhanh);
                            bundle.putString("mo_ta", mo_ta);
                            bundle.putString("ngaythuhoach", ngaythuhoach);
                            bundle.putString("tenloai", tenloai);
                            bundle.putDouble("trong_luong", trong_luong);
                            bundle.putString("xuat_xu", xuat_xu);
                            bundle.putString("maQr", maQr);

                            GD_lich_su_quet_ma detailFragment = new GD_lich_su_quet_ma();
                            detailFragment.setArguments(bundle);

                            // Create AlertDialog to display all information
                            ////////////////////////////////////
                            // Inflate layout cho dialog thứ hai
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_layout, null);

                            // Set data to views
                            TextView detailTitle = dialogView.findViewById(R.id.detailTitle);
                            ImageView detailImage = dialogView.findViewById(R.id.detailImage);
                            ImageView ivQRCode = dialogView.findViewById(R.id.ivQRCode);
                            TextView detailName = dialogView.findViewById(R.id.detailName);
                            TextView detailOrigin = dialogView.findViewById(R.id.detailOrigin);
                            TextView detailHarvestDate = dialogView.findViewById(R.id.detailHarvestDate);
                            TextView detailWeight = dialogView.findViewById(R.id.detailWeight);
                            TextView detailDesc = dialogView.findViewById(R.id.detailDesc);

                            detailTitle.setText("ẢNH CAM");

                            // Assuming hinhanh is a valid URI
                            Glide.with(dialogView).load(hinhanh).into(detailImage);

                            // Load QR code using Glide
                            Glide.with(dialogView).load(maQr).into(ivQRCode);

                            detailName.setText("THỂ LOẠI: "+tenloai);
                            detailOrigin.setText("XUẤT XỨ: "+xuat_xu);
                            detailHarvestDate.setText("NGÀY THU HOACH: "+ngaythuhoach);
                            detailWeight.setText("TRỌNG LƯỢNG: "+(String.valueOf(trong_luong)));
                            detailDesc.setText("MÔ TẢ: "+mo_ta);
                            // Initialize Firebase Database
                            databaseReference2 = FirebaseDatabase.getInstance().getReference("lichSuQuet");
                            // Create an instance of LichSuQuet
                            LichSuQuet lichSuQuet = new LichSuQuet(
                                    maqr,
                                    hinhanh,
                                    mo_ta,
                                    ngaythuhoach,
                                    tenloai,
                                    trong_luong,
                                    xuat_xu,
                                    maQr
                            );

                            // Save the instance to Firebase
                            saveLichSuQuetToFirebase(lichSuQuet);

                            // Tạo AlertDialog thứ hai với layout tùy chỉnh
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(requireContext());
                            builder2.setView(dialogView)
                                    .setTitle("THÔNG TIN")
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        // User clicked OK button
                                    })
                                    .setNegativeButton("Cancel", (dialog, id) -> {
                                        // User cancelled the dialog
                                        dialog.dismiss();
                                    });

                            // Hiển thị AlertDialog thứ hai
                            AlertDialog alertDialog2 = builder2.create();
                            alertDialog2.show();
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseCheck", "Lỗi khi lấy dữ liệu từ Firebase: " + e.getMessage());
                    }
                } else {
                    // Hiển thị Dialog thông báo không tìm thấy ID
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Thông tin mã QR");
                    builder.setMessage("ID không tồn tại trong cơ sở dữ liệu.");
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss(); // Đóng Dialog khi nút "Đóng" được nhấn
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("FirebaseCheck", "Lỗi cơ sở dữ liệu: " + databaseError.getMessage());
            }
        });
    }
    ////////////////////////////////////////////
    private void saveLichSuQuetToFirebase(LichSuQuet lichSuQuet) {
        // Get a unique key for the new record
        String key = databaseReference2.push().getKey();
        lichSuQuet.setKey(key);

        // Save the record to Firebase
        databaseReference2.child(key).setValue(lichSuQuet)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "lỗi khi lưu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //////////////////////////////////////////////
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_scan, menu); // Nạp tệp XML menu_scan vào đối tượng Menu
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.open_from_gallery) {
            // Xử lý sự kiện khi người dùng chọn mục "open_from_gallery"
            openGallery();
            return true;
        } else if (itemId == R.id.flash_toggle) {
            toggleFlash();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openGallery() {
        // Mở thư viện ảnh
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void toggleFlash() {
        // Xử lý bật/tắt đèn flash
        Toast.makeText(requireContext(), "Flash toggled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        // Khởi tạo và cấu hình CameraX
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraPreview(ProcessCameraProvider cameraProvider) {
        PreviewView previewView = requireView().findViewById(R.id.previewView); // Tham chiếu đến PreviewView từ giao diện người dùng

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Khởi chạy CameraX và kết nối với PreviewView
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // Thay thế fragment trong frame_layout (id được xác định trong file layout) bằng fragment mới
        fragmentTransaction.addToBackStack(null); // Thêm transaction vào back stack để có thể quay lại Fragment trước đó khi cần
        fragmentTransaction.commit(); // Xác nhận giao dịch và áp dụng thay đổi
    }
}
