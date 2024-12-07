package com.example.dogmatch2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DoctorAdapter extends ArrayAdapter<Doctor> {

    public DoctorAdapter(Context context, int resourceId, List<Doctor> items) {
        super(context, resourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Doctor doctor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_doctor, parent, false);
        }

        ImageView doctorImage = convertView.findViewById(R.id.doctorImage);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvSpecialty = convertView.findViewById(R.id.tvSpecialty);
        TextView tvDistance = convertView.findViewById(R.id.tvDistance);

        tvName.setText(doctor.getName());
        tvSpecialty.setText(doctor.getSpecialty());
        tvDistance.setText(String.format("%.2f km", doctor.getDistance()));

        Glide.with(getContext()).load(doctor.getImageUrl()).into(doctorImage);

        return convertView;
    }
}
