package com.alriftech.sikbk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class KantorHolder extends RecyclerView.ViewHolder {
    private TextView txtLatLang, txtNamaKantor, txtWaktu, txtAlamat;

    public KantorHolder(View itemView) {
        super(itemView);

        this.txtLatLang     = itemView.findViewById(R.id.txt_list_kantor_latlang);
        this.txtNamaKantor  = itemView.findViewById(R.id.txt_list_kantor_nama_kantor);
        this.txtWaktu       = itemView.findViewById(R.id.txt_list_kantor_waktu);
        this.txtAlamat      = itemView.findViewById(R.id.txt_list_kantor_alamat);
    }

    private String latLangFormat(double lat, double lang) {
        NumberFormat fLat = new DecimalFormat("#0.000");
        NumberFormat fLang = new DecimalFormat("#0.000");

        return fLat.format(lat) + ", " + fLang.format(lang);
    }

    public void setDetails(final Kantor kantor) {
        txtLatLang.setText(latLangFormat(kantor.getLatitude(), kantor.getLongitude()));
        txtNamaKantor.setText(kantor.getNama_kantor());
        txtWaktu.setText(kantor.getJam_buka() + " - " + kantor.getJam_tutup());
        txtAlamat.setText(kantor.getAlamat());

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context c = itemView.getRootView().getContext();
//
//                sp = c.getSharedPreferences("SIKBK", Context.MODE_PRIVATE);
//                sp.edit().putInt("id_lowongan", lowongan.getId()).apply();
//
//                c.startActivity(new Intent(c, DetailLowonganActivity.class));
//            }
//        });
    }
}
