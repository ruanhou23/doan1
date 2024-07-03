package com.example.app_qr_code_chinh.QL_nguoidung;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.MainActivity;
import com.example.app_qr_code_chinh.QL_cam_sanh.GD_activity_update;
import com.example.app_qr_code_chinh.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GD_activity_detail_user extends Fragment {
    private ImageView avartar_detail;
    private TextView ten_detail, trangthai_detail, email_detail, matkhau_detail, tt_detail;
    private String imageUrl,key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_activity_detail_user, container, false);
        avartar_detail = view.findViewById(R.id.detailImage);
        ten_detail = view.findViewById(R.id.ten_detail);
        trangthai_detail = view.findViewById(R.id.trangthai_detail);
        email_detail = view.findViewById(R.id.email_detail);
        matkhau_detail = view.findViewById(R.id.matkhau_detail);
        tt_detail = view.findViewById(R.id.tt_detail);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("Image");
            String title = getArguments().getString("Name");
            String description = getArguments().getString("Status");
            String email = getArguments().getString("Email");
            String password = getArguments().getString("Password");
            String tt = getArguments().getString("TT");
            key = getArguments().getString("Key"); // Ensure the key is retrieved here

            Glide.with(this).load(imageUrl).into(avartar_detail);
            ten_detail.setText(title);
            trangthai_detail.setText(description);
            email_detail.setText(email);
            matkhau_detail.setText(password);
            tt_detail.setText(tt);
        }
        Bundle bundle = getArguments();
        View deleteButton = view.findViewById(R.id.deleteButton);  // Assuming delete button ID is deleteButton
        View editButton = view.findViewById(R.id.editButton);  // Assuming edit button ID is editButton
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NguoiDung");
                key = bundle.getString("Key"); // Lấy key để xác định dữ liệu cụ thể cần xóa

                // Xóa hình ảnh từ Firebase Storage
                String imageUrl = bundle.getString("Image");
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Hình ảnh đã được xóa thành công, tiếp tục xóa dữ liệu từ Firebase Realtime Database
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        });
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepare data to pass to the edit fragment
                Bundle args = new Bundle();
                args.putString("anh", imageUrl);
                args.putString("Name", ten_detail.getText().toString());
                args.putString("Status", trangthai_detail.getText().toString());
                args.putString("Email", email_detail.getText().toString());
                args.putString("Password", matkhau_detail.getText().toString());
                args.putString("TT", tt_detail.getText().toString());
                args.putString("key",key);

                // Navigate to the edit fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                GD_activity_update_user newFragment = new GD_activity_update_user();
                newFragment.setArguments(args);
                fragmentTransaction.replace(R.id.container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
