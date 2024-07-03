package com.example.app_qr_code_chinh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_qr_code_chinh.QL_nguoidung.GD_activity_upload_user;
import com.example.app_qr_code_chinh.adapter.UserAdapter;
import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GD_quan_ly_nguoi_dung extends Fragment {

    FloatingActionButton fab; // Nút hành động nổi
    DatabaseReference databaseReference; // Tham chiếu tới Firebase Database
    ValueEventListener eventListener; // Lắng nghe sự kiện thay đổi dữ liệu từ Firebase
    RecyclerView recyclerView; // RecyclerView để hiển thị danh sách dữ liệu
    List<NguoiDung> userList; // Danh sách dữ liệu
    UserAdapter userAdapter; // Adapter cho RecyclerView
    SearchView searchView; // Thanh tìm kiếm

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_d_quan_ly_nguoi_dung, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_user);
        fab = view.findViewById(R.id.fab);
        searchView = view.findViewById(R.id.search);

        // Tạo GridLayoutManager với 1 cột cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        searchView.clearFocus();

        // Xây dựng AlertDialog để hiển thị tiến trình
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.item_layout_user);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Khởi tạo ArrayList để lưu dữ liệu và Adapter cho RecyclerView
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(), userList);
        recyclerView.setAdapter(userAdapter);

        // Tham chiếu đến Firebase Database với đường dẫn "NguoiDung"
        databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");

        // Thêm ValueEventListener để lắng nghe sự thay đổi dữ liệu trên Firebase Database
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear(); // Xóa dữ liệu cũ
                // Duyệt qua các phần tử con của snapshot
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    NguoiDung dataClass = itemSnapshot.getValue(NguoiDung.class);
                    if (dataClass != null) {
                        dataClass.setKey(itemSnapshot.getKey()); // Thiết lập khóa của đối tượng
                        userList.add(dataClass); // Thêm đối tượng vào danh sách
                    }
                }
                userAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                dialog.dismiss(); // Đóng dialog hiển thị tiến trình
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss(); // Đóng dialog nếu có lỗi xảy ra
            }
        });

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

        // Xử lý sự kiện khi nhấn vào nút "Thêm User"
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GD_activity_upload_user newFragment = new GD_activity_upload_user();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
    // Tìm kiếm dữ liệu trong danh sách
    public void searchList(String text) {
        ArrayList<NguoiDung> searchList = new ArrayList<>();
        for (NguoiDung nguoiDung : userList) { // Iterate over userList
            if (nguoiDung.getTen().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(nguoiDung);
            }
        }
        userAdapter.searchDataList(searchList);
    }
}
