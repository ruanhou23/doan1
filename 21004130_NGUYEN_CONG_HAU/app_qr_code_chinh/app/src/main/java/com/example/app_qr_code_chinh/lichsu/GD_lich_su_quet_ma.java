package com.example.app_qr_code_chinh.lichsu;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.adapter.LichSuAdapter;
import com.example.app_qr_code_chinh.classdata.LichSuQuet;
import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.Toast;

public class GD_lich_su_quet_ma extends Fragment {

    // Variables to store data from the Bundle
    private String idcam, hinhanh, mo_ta, ngaythuhoach, tenloai, xuat_xu, maQr;
    private double trong_luong;
    private DatabaseReference databaseReference; // Reference to Firebase Database
    private ValueEventListener eventListener; // Listener for Firebase data changes
    private ChildEventListener childEventListener;
    private RecyclerView recyclerView; // RecyclerView to display the data list
    private List<LichSuQuet> lichsuList; // List to store the data
    private LichSuAdapter lichsuAdapter; // Adapter for the RecyclerView
    private AlertDialog dialog; // Progress dialog

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g_d_lich_su_quet_ma, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_lich_su);


        // Set up GridLayoutManager with 1 column for the RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Build AlertDialog to show progress
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.item_layout_user); // Assuming you have a layout for the progress dialog
        dialog = builder.create();
        dialog.show();

        // Initialize ArrayList to store data and Adapter for the RecyclerView
        lichsuList = new ArrayList<>();
        lichsuAdapter = new LichSuAdapter(requireContext(), lichsuList);
        recyclerView.setAdapter(lichsuAdapter);

        // Reference to Firebase Database with path "lichSuQuet"
        databaseReference = FirebaseDatabase.getInstance().getReference("lichSuQuet");

//        // Add ValueEventListener to listen for data changes in Firebase Database
//        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                lichsuList.clear(); // Xóa dữ liệu cũ
//                // Duyệt qua các phần tử con của snapshot
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
//                    LichSuQuet dataClass = itemSnapshot.getValue(LichSuQuet.class);
//                    if (dataClass != null) {
//                        dataClass.setKey(itemSnapshot.getKey()); // Thiết lập khóa của đối tượng
//                        lichsuList.add(dataClass); // Thêm đối tượng vào danh sách
//                    }
//                }
//                lichsuAdapter.notifyDataSetChanged();// Cập nhật RecyclerView
//                dialog.dismiss(); // Đóng dialog hiển thị tiến trình
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Log the database error and notify the user
//                Log.e("FirebaseError", "Database error: " + error.getMessage(), error.toException());
//                Toast.makeText(getContext(), "An error occurred while retrieving data: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                dialog.dismiss(); // Close progress dialog if there's an error
//            }
//        });
        // Add ChildEventListener to listen for data changes in Firebase Database
        childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                LichSuQuet dataClass = snapshot.getValue(LichSuQuet.class);
                if (dataClass != null) {
                    dataClass.setKey(snapshot.getKey());
                    lichsuList.add(dataClass);
                    lichsuAdapter.notifyItemInserted(lichsuList.size() - 1);
                }
                dialog.dismiss(); // Close dialog after first data load
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                LichSuQuet dataClass = snapshot.getValue(LichSuQuet.class);
                if (dataClass != null) {
                    String key = snapshot.getKey();
                    for (int i = 0; i < lichsuList.size(); i++) {
                        if (lichsuList.get(i).getKey().equals(key)) {
                            lichsuList.set(i, dataClass);
                            lichsuAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                for (int i = 0; i < lichsuList.size(); i++) {
                    if (lichsuList.get(i).getKey().equals(key)) {
                        lichsuList.remove(i);
                        lichsuAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the database error and notify the user
                Log.e("FirebaseError", "Database error: " + error.getMessage(), error.toException());
                Toast.makeText(getContext(), "An error occurred while retrieving data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss(); // Close progress dialog if there's an error
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the Firebase event listener when the view is destroyed to prevent memory leaks
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}