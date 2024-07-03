package com.example.app_qr_code_chinh;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class GD_yeu_cau_dang_nhap extends Fragment {
    private Button login;
    // Interface for button click event
    public interface OnLoginButtonClickListener {
        void onLoginButtonClick();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_yeu_cau_dang_nhap, container, false);
        login = view.findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the interface method
                if (getActivity() instanceof OnLoginButtonClickListener) {
                    ((OnLoginButtonClickListener) getActivity()).onLoginButtonClick();
                }
            }
        });
        return view;
    }
}