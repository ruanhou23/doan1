package com.example.app_qr_code_chinh.lichsu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.R;


public class GD_thong_tin_quet_ma extends Fragment {
    private TextView detailTitle, detailName, detailOrigin, detailHarvestDate, detailWeight, detailDesc;
    private ImageView detailImage, ivQRCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_thong_tin_quet_ma, container, false);
        // Ánh xạ các view từ XML
        detailTitle = view.findViewById(R.id.detailTitle);
        detailName = view.findViewById(R.id.detailName);
        detailOrigin = view.findViewById(R.id.detailOrigin);
        detailHarvestDate = view.findViewById(R.id.detailHarvestDate);
        detailWeight = view.findViewById(R.id.detailWeight);
        detailDesc = view.findViewById(R.id.detailDesc);
        detailImage = view.findViewById(R.id.detailImage);
        ivQRCode = view.findViewById(R.id.ivQRCode);
        // Get data from bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String imageUrl = bundle.getString("ImageUrl");
            String name = bundle.getString("Name");
            String origin = bundle.getString("Origin");
            String harvestDate = bundle.getString("HarvestDate");
            String weight = bundle.getString("Weight");
            String description = bundle.getString("Description");
            String qrCode = bundle.getString("QrCode");
            String key = bundle.getString("Key");

            // Set data to views
            detailTitle.setText(name);
            detailName.setText(name);
            detailOrigin.setText(origin);
            detailHarvestDate.setText(harvestDate);
            detailWeight.setText(weight);
            detailDesc.setText(description);

            // Load images using a library like Glide or Picasso
            // For example, using Glide:
            Glide.with(this).load(imageUrl).into(detailImage);
            Glide.with(this).load(qrCode).into(ivQRCode);
        }
        return view;
    }
}