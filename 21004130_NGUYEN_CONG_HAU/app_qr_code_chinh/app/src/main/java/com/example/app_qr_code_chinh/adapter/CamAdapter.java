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
import com.example.app_qr_code_chinh.QL_cam_sanh.GD_activity_detail;
import com.example.app_qr_code_chinh.R;
import com.example.app_qr_code_chinh.classdata.Cam;

import java.util.ArrayList;
import java.util.List;

public class CamAdapter extends RecyclerView.Adapter<CamAdapter.CamViewHolder> {

    private List<Cam> camList;
    private Context context;

    public CamAdapter(Context context, List<Cam> camList) {
        this.context = context;
        this.camList = camList;
    }

    @NonNull
    @Override
    public CamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_camsanh, parent, false);
        return new CamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CamViewHolder holder, int position) {
        Cam cam = camList.get(position);
        if (cam.getHinhAnhCam() != null) {
            Glide.with(context).load(cam.getHinhAnhCam()).into(holder.hinhAnhCam);
        }
        holder.tenLoaiCam.setText(cam.getTenLoai());
        holder.xuatXuCam.setText(cam.getXuatXu());
        holder.ngayThuHoachCam.setText(cam.getNgayThuHoach());
        holder.trongLuongCam.setText(String.valueOf(cam.getTrongLuong()));
        holder.moTaCam.setText(cam.getMoTa());
        holder.ngayTaoCam.setText(cam.getNgayTao());

        holder.theCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("ImageUrl", cam.getHinhAnhCam());
                bundle.putString("Name", cam.getTenLoai());
                bundle.putString("Origin", cam.getXuatXu());
                bundle.putString("HarvestDate", cam.getNgayThuHoach());
                bundle.putString("Weight", String.valueOf(cam.getTrongLuong()));
                bundle.putString("Description", cam.getMoTa());
                bundle.putString("CreatedDate", cam.getNgayTao());
                bundle.putString("QrCode", cam.getMaQr());
                bundle.putString("Key", cam.getIdcam());

                GD_activity_detail detailFragment = new GD_activity_detail();
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
        return camList.size();
    }

    public void searchDataList(ArrayList<Cam> searchList) {
        camList = searchList;
        notifyDataSetChanged();
    }

    static class CamViewHolder extends RecyclerView.ViewHolder {
        ImageView hinhAnhCam;
        TextView tenLoaiCam, xuatXuCam, ngayThuHoachCam, trongLuongCam, moTaCam, ngayTaoCam, maQrCam;
        CardView theCam;

        public CamViewHolder(@NonNull View itemView) {
            super(itemView);
            theCam = itemView.findViewById(R.id.recCard_camsanh_xml);
            hinhAnhCam = itemView.findViewById(R.id.hinh_anh_cam_xml);
            tenLoaiCam = itemView.findViewById(R.id.ten_loai_cam_xml);
            xuatXuCam = itemView.findViewById(R.id.xuat_xu_cam_xml);
            ngayThuHoachCam = itemView.findViewById(R.id.ngay_thu_hoach_cam_xml);
            trongLuongCam = itemView.findViewById(R.id.trong_luong_cam_xml);
            moTaCam = itemView.findViewById(R.id.mo_ta_cam_xml);
            ngayTaoCam = itemView.findViewById(R.id.ngay_tao_cam_xml);
        }
    }
}