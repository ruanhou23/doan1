package com.example.app_qr_code_chinh;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_qr_code_chinh.QL_cam_sanh.GD_activity_upload;
import com.example.app_qr_code_chinh.adapter.CamAdapter;
import com.example.app_qr_code_chinh.adapter.UserAdapter;
import com.example.app_qr_code_chinh.classdata.Cam;
import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GD_quan_ly_thong_tin_cam_sanh extends Fragment {
    FloatingActionButton fab; // Nút hành động nổi
    DatabaseReference databaseReference; // Tham chiếu tới Firebase Database
    ValueEventListener eventListener; // Lắng nghe sự kiện thay đổi dữ liệu từ Firebase
    RecyclerView recyclerView; // RecyclerView để hiển thị danh sách dữ liệu
    List<Cam> camList; // Danh sách dữ liệu
    CamAdapter camAdapter; // Adapter cho RecyclerView
    SearchView searchView; // Thanh tìm kiếm

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_g_d_quan_ly_thong_tin_cam_sanh, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_camsanh);
        fab = view.findViewById(R.id.fab);
        searchView = view.findViewById(R.id.search);

        //////////////////////////////////////////
        // Tạo GridLayoutManager với 1 cột cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        searchView.clearFocus();

        // Xây dựng AlertDialog để hiển thị tiến trình
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Khởi tạo ArrayList để lưu dữ liệu và Adapter cho RecyclerView
        camList = new ArrayList<>();
        camAdapter = new CamAdapter(requireContext(), camList);
        recyclerView.setAdapter(camAdapter);

        // Tham chiếu đến Firebase Database với đường dẫn "Cam"
        databaseReference = FirebaseDatabase.getInstance().getReference("Cam");

        // Thêm ValueEventListener để lắng nghe sự thay đổi dữ liệu trên Firebase Database
        // Thêm ValueEventListener để lắng nghe sự thay đổi dữ liệu trên Firebase Database
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                camList.clear();  // Clear the list to avoid duplications
                // Duyệt qua các phần tử con của snapshot
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Cam dataClass = itemSnapshot.getValue(Cam.class);
                    if (dataClass != null) {
                        dataClass.setKey(itemSnapshot.getKey()); // Thiết lập khóa của đối tượng
                        camList.add(dataClass); // Thêm đối tượng vào danh sách
                    }
                }
                camAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                dialog.dismiss(); // Đóng dialog hiển thị tiến trình
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss(); // Đóng dialog hiển thị tiến trình
            }
        });
        /////////////////////////////////////
        // Thiết lập sự kiện lắng nghe cho thanh tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không thực hiện hành động khi người dùng nhấn nút tìm kiếm
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText); // Tìm kiếm dữ liệu khi văn bản thay đổi
                return true;
            }
        });

        // Thiết lập sự kiện lắng nghe khi nút hành động nổi được nhấn
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển sang fragment upload khi người dùng nhấn nút
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                GD_activity_upload newFragment = new GD_activity_upload();
                fragmentTransaction.replace(R.id.container, newFragment);
                fragmentTransaction.addToBackStack(null); // Thêm vào backstack
                fragmentTransaction.commit(); // Thực hiện giao dịch
            }
        });
        return view;
    }

    // Tìm kiếm dữ liệu trong danh sách
    public void searchList(String text) {
        ArrayList<Cam> searchList = new ArrayList<>(); // Tạo danh sách tạm thời để lưu kết quả tìm kiếm
        for (Cam cam : camList) {
            // Kiểm tra xem tiêu đề dữ liệu có chứa văn bản tìm kiếm hay không
            if (cam.getTenLoai().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(cam); // Thêm vào danh sách kết quả tìm kiếm
            }
        }
        camAdapter.searchDataList(searchList); // Cập nhật dữ liệu tìm kiếm cho adapter
    }
}