package com.example.app_qr_code_chinh.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.QL_nguoidung.GD_activity_detail_user;
import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.NguoiDung;
import com.example.app_qr_code_chinh.QL_cam_sanh.GD_activity_detail;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private List<NguoiDung> userList;
    private Context context;

    public UserAdapter(Context context, List<NguoiDung> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        NguoiDung nguoiDung = userList.get(position);
        Glide.with(context).load(nguoiDung.getUrlHinhAnh()).into(holder.recImage);
        holder.name.setText(nguoiDung.getTen());
        holder.password.setText(nguoiDung.getMatKhau());
        holder.email.setText(nguoiDung.getEmail());
        holder.status.setText(nguoiDung.getTrangThai());
        holder.createdDate.setText(nguoiDung.getNgayTao());
        holder.updatedDate.setText(nguoiDung.getNgayCapNhat());
        holder.tt.setText(nguoiDung.getTT());
        holder.tr.setText(nguoiDung.getTr());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Image", nguoiDung.getUrlHinhAnh());
                bundle.putString("Name", nguoiDung.getTen());
                bundle.putString("Password", nguoiDung.getMatKhau());
                bundle.putString("Email", nguoiDung.getEmail());
                bundle.putString("Status", nguoiDung.getTrangThai());
                bundle.putString("CreatedDate", nguoiDung.getNgayTao());
                bundle.putString("UpdatedDate", nguoiDung.getNgayCapNhat());
                bundle.putString("TT", nguoiDung.getTT());
                bundle.putString("Tr", nguoiDung.getTr());
                bundle.putString("Key", nguoiDung.getKey());

                GD_activity_detail_user detailFragment = new GD_activity_detail_user();
                detailFragment.setArguments(bundle);

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void searchDataList(ArrayList<NguoiDung> searchList) {
        userList = searchList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage;
        TextView name, password, email, status, createdDate, updatedDate, tt, tr;
        CardView cardView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.avartar_user_xml);
            cardView = itemView.findViewById(R.id.recCard_user_xml);
            name = itemView.findViewById(R.id.ten_user_xml);
            password = itemView.findViewById(R.id.matkhau_user_xml);
            email = itemView.findViewById(R.id.email_user_xml);
            status = itemView.findViewById(R.id.trangthai_user_xml);
            createdDate = itemView.findViewById(R.id.ngaytao_user_xml);
            updatedDate = itemView.findViewById(R.id.ngaycapnhat_user_xml);
            tt = itemView.findViewById(R.id.tt_user_xml);
            tr = itemView.findViewById(R.id.tr_user_xml);
        }
    }
}
