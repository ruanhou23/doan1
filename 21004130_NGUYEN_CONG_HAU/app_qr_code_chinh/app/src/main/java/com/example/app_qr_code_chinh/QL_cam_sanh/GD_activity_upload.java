package com.example.app_qr_code_chinh.QL_cam_sanh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.fragment.app.Fragment;

import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.AESEncryption;
import com.example.app_qr_code_chinh.classdata.Cam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Random;

import com.example.app_qr_code_chinh.classdata.DESUtil;

public class GD_activity_upload extends Fragment {
    private EditText inputTenLoai;
    private EditText inputXuatXu;
    private EditText inputNgayThuHoach;
    private EditText inputTrongLuong;
    private EditText inputMoTa;
    private ImageView ivQRCode;
    private ImageView uploadImageCam;
    private Button taoCam, luucam;
    private Uri uri;
    private Bitmap bitmap;
    private String idcam, tenLoai, xuatXu, ngayThuHoach, moTa, maQr, ngayTao, hinhAnhCam, maccamsanh, fileName, urlImage;
    private double trongLuong;

    private PublicKey publicKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_d_activity_upload, container, false);
        inputTenLoai = view.findViewById(R.id.input_tenLoai_xml);
        inputXuatXu = view.findViewById(R.id.input_xuatXu_xml);
        inputNgayThuHoach = view.findViewById(R.id.input_ngayThuHoach_xml);
        inputTrongLuong = view.findViewById(R.id.input_trongLuong_xml);
        inputMoTa = view.findViewById(R.id.input_moTa_xml);
        ivQRCode = view.findViewById(R.id.ivQRCode);
        uploadImageCam = view.findViewById(R.id.uploadImage_cam);
        taoCam = view.findViewById(R.id.tao_cam_xml);
        luucam = view.findViewById(R.id.luu_TT_xml);

        luucam.setVisibility(View.GONE);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                uri = data.getData();
                                uploadImageCam.setImageURI(uri);
                            } else {
                                Toast.makeText(getActivity(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImageCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        setupSubmitButton();
        return view;
    }

    private void setupSubmitButton() {
        taoCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenLoai = inputTenLoai.getText().toString();
                xuatXu = inputXuatXu.getText().toString();
                ngayThuHoach = inputNgayThuHoach.getText().toString();
                moTa = inputMoTa.getText().toString();
                try {
                    trongLuong = Double.parseDouble(inputTrongLuong.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Trọng lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                taomaqr();
                luucam.setVisibility(View.VISIBLE);
            }
        });

        luucam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    uploadImageToFirebase(uri);
                    saveImageToGallery(bitmap);
                } else {
                    Toast.makeText(getActivity(), "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageToFirebase(Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Cam Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                hinhAnhCam = urlImage.toString();
                dialog.dismiss();
                saveUserData(); // Lưu dữ liệu người dùng sau khi tải ảnh thành công
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        idcam = maccamsanh;
        ngayTao = String.valueOf(System.currentTimeMillis());
        Cam cam = new Cam(idcam, tenLoai, xuatXu, ngayThuHoach, trongLuong, moTa, ngayTao, maQr, hinhAnhCam);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Cam");

        reference.child(idcam).setValue(cam).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    inputTenLoai.setText("");
                    inputXuatXu.setText("");
                    inputNgayThuHoach.setText("");
                    inputTrongLuong.setText("");
                    inputMoTa.setText("");
                    ivQRCode.setImageResource(R.drawable.uploadimg);
                    uploadImageCam.setImageResource(R.drawable.uploadimg);

                    Toast.makeText(getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void taomaqr() {
        maccamsanh = taomangaunhien();
        String textToEncode = "type_cam_sanh:" + maccamsanh;

        try {
            String encryptedTextAESE = AESEncryption.encrypt(textToEncode);
            String encryptedText = DESUtil.encryptDES(encryptedTextAESE);
            if (!textToEncode.isEmpty()) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    bitmap = barcodeEncoder.encodeBitmap(encryptedText, BarcodeFormat.QR_CODE, 400, 400);
                    ivQRCode.setImageBitmap(bitmap);
                    saveBitmapToFirebase(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBitmapToFirebase(Bitmap bitmap) {
        File file = new File(getContext().getCacheDir(), "qr_code.png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            uploadFileToFirebase(Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("qr_codes/" + fileUri.getLastPathSegment());

        storageReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlImage = uri.toString();
                        maQr = urlImage; // Assign the QR code URL to maQr
                        Toast.makeText(getActivity(), "QR code uploaded successfully: " + urlImage, Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToGallery(Bitmap bitmap) {
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        fileName = "QR_" + System.currentTimeMillis() + ".png";
        File file = new File(picturesDir, fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();

            MediaScannerConnection.scanFile(requireContext(), new String[]{file.toString()}, null, (path, uri) -> {
                Toast.makeText(requireContext(), "QR code đã được lưu vào thư viện.", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String taomangaunhien() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10); // Generate random digit from 0 to 9
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }
}
