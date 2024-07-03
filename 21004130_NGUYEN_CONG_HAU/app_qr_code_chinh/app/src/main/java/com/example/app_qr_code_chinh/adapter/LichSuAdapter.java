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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.LichSuQuet;
import com.example.app_qr_code_chinh.lichsu.GD_thong_tin_quet_ma;

import java.util.List;

public class LichSuAdapter extends RecyclerView.Adapter<LichSuAdapter.LichSuViewHolder> {
    private List<LichSuQuet> lichsuList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public LichSuAdapter(Context context, List<LichSuQuet> lichsuList) {
        this.context = context;
        this.lichsuList = lichsuList;
    }

    @NonNull
    @Override
    public LichSuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_lichsu, parent, false);
        return new LichSuViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LichSuViewHolder holder, int position) {
        LichSuQuet lichSuQuet = lichsuList.get(position);

        if (lichSuQuet != null) {
            Glide.with(context).load(lichSuQuet.getHinhanh()).into(holder.hinhAnhCam);

            holder.tenLoaiCam.setText(lichSuQuet.getTenLoai());
            holder.xuatXuCam.setText(lichSuQuet.getXuatXu());
            holder.ngayThuHoachCam.setText(lichSuQuet.getNgayThuHoach());
            holder.trongLuongCam.setText(String.valueOf(lichSuQuet.getTrongLuong()));
            holder.moTaCam.setText(lichSuQuet.getMoTa());

            holder.theCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ImageUrl", lichSuQuet.getHinhanh());
                    bundle.putString("Name", lichSuQuet.getTenLoai());
                    bundle.putString("Origin", lichSuQuet.getXuatXu());
                    bundle.putString("HarvestDate", lichSuQuet.getNgayThuHoach());
                    bundle.putString("Weight", String.valueOf(lichSuQuet.getTrongLuong()));
                    bundle.putString("Description", lichSuQuet.getMoTa());
                    bundle.putString("QrCode", lichSuQuet.getMaQr());
                    bundle.putString("Key", lichSuQuet.getIdqr());

                    GD_thong_tin_quet_ma detailFragment = new GD_thong_tin_quet_ma();
                    detailFragment.setArguments(bundle);

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            holder.theCam.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemDelete(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lichsuList.size();
    }

    static class LichSuViewHolder extends RecyclerView.ViewHolder {
        ImageView hinhAnhCam;
        TextView tenLoaiCam, xuatXuCam, ngayThuHoachCam, trongLuongCam, moTaCam;
        CardView theCam;

        LichSuViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            theCam = itemView.findViewById(R.id.recCard_lichsu_xml); // Corrected to match XML ID
            hinhAnhCam = itemView.findViewById(R.id.hinh_anh_cam_xml);
            tenLoaiCam = itemView.findViewById(R.id.ten_loai_cam_xml);
            xuatXuCam = itemView.findViewById(R.id.xuat_xu_cam_xml);
            ngayThuHoachCam = itemView.findViewById(R.id.ngay_thu_hoach_cam_xml);
            trongLuongCam = itemView.findViewById(R.id.trong_luong_cam_xml);
            moTaCam = itemView.findViewById(R.id.mo_ta_cam_xml);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemDelete(position);
                        }
                    }
                }
            });
        }
    }
}