package com.example.app_qr_code_chinh.QL_cam_sanh;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.app_qr_code_chinh.QL_nguoidung.GD_activity_update_user;
import com.example.app_qr_code_chinh.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GD_activity_detail extends Fragment {

    private String imageUrl,qrCode;
    private String key;

    private TextView detailTitle;
    private ImageView detailImage,ivQRCode;
    private TextView detailName;
    private TextView detailOrigin;
    private TextView detailHarvestDate;
    private TextView detailWeight;
    private TextView detailDesc;
    private TextView detailCreatedDate;
    private TextView detailQrCode;
    private View deleteButton;
    private View editButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_activity_detail, container, false);

        // Initialize the views
        detailTitle = view.findViewById(R.id.detailTitle);
        detailImage = view.findViewById(R.id.detailImage);
        detailName = view.findViewById(R.id.detailName);
        detailOrigin = view.findViewById(R.id.detailOrigin);
        detailHarvestDate = view.findViewById(R.id.detailHarvestDate);
        detailWeight = view.findViewById(R.id.detailWeight);
        detailDesc = view.findViewById(R.id.detailDesc);
        detailCreatedDate = view.findViewById(R.id.detailCreatedDate);
        detailQrCode = view.findViewById(R.id.detailQrCode);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);
        ivQRCode = view.findViewById(R.id.ivQRCode);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("ImageUrl");
            String name = getArguments().getString("Name");
            String origin = getArguments().getString("Origin");
            String harvestDate = getArguments().getString("HarvestDate");
            String weight = getArguments().getString("Weight");
            String description = getArguments().getString("Description");
            String createdDate = getArguments().getString("CreatedDate");
            qrCode = getArguments().getString("QrCode");
            key = getArguments().getString("Key"); // Ensure the key is retrieved here

            // Load image using Glide
            Glide.with(this).load(imageUrl).into(detailImage);
            Glide.with(this).load(qrCode).into(ivQRCode);

            // Set text for other details
            detailName.setText(name);
            detailOrigin.setText(origin);
            detailHarvestDate.setText(harvestDate);
            detailWeight.setText(weight);
            detailDesc.setText(description);
            detailCreatedDate.setText(createdDate);
            detailQrCode.setText(qrCode);
        }

        // Set up the click listeners
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepare data to pass to the edit fragment
                Bundle args = new Bundle();
                args.putString("ImageUrl", imageUrl);
                args.putString("Name", detailName.getText().toString());
                args.putString("Origin", detailOrigin.getText().toString());
                args.putString("HarvestDate", detailHarvestDate.getText().toString());
                args.putString("Weight", detailWeight.getText().toString());
                args.putString("Description", detailDesc.getText().toString());
                args.putString("CreatedDate", detailCreatedDate.getText().toString());
                args.putString("QrCode", detailQrCode.getText().toString());
                args.putString("Key", key);

                // Navigate to the edit fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                GD_activity_update newFragment = new GD_activity_update();
                newFragment.setArguments(args);
                fragmentTransaction.replace(R.id.container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void deleteItem() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cam");

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Hình ảnh đã được xóa thành công, tiếp tục xóa dữ liệu từ Firebase Realtime Database
                reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}