package com.alriftech.sikbk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LowonganHolder extends RecyclerView.ViewHolder {
    SharedPreferences sp;
    private TextView txtBidang, txtKantor, txtDeskripsi, txtJumlah, txtWaktu, txtKategori, txtJarak;
    private TextView txtDis;
    private ImageView imgDis;

    public LowonganHolder(View itemView) {
        super(itemView);

        this.txtBidang      = itemView.findViewById(R.id.txt_list_bidang);
        this.txtKantor      = itemView.findViewById(R.id.txt_list_kantor);
        this.txtDeskripsi   = itemView.findViewById(R.id.txt_list_deskripsi);
        this.txtJumlah      = itemView.findViewById(R.id.txt_list_jumlah);
        this.txtWaktu       = itemView.findViewById(R.id.txt_list_waktu);
        this.txtKategori    = itemView.findViewById(R.id.txt_list_kategori);
        this.txtJarak       = itemView.findViewById(R.id.txt_list_jarak);

        this.txtDis         = itemView.findViewById(R.id.row_lay_disable_text);
        this.imgDis         = itemView.findViewById(R.id.row_lay_disable_bg);
    }

    private long cekWaktu(String tanggal) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm");
        Date date = null;

        try {
            date = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time1 = date.getTime();
        long time2 = System.currentTimeMillis();
        long timemilis = time1 - time2;

        return timemilis;
    }

    public void setDetails(final Lowongan lowongan) {
        txtBidang.setText(lowongan.getBidang());
        txtKantor.setText(lowongan.getKantor());
        txtDeskripsi.setText(lowongan.getDeskripsi());
        txtJumlah.setText(lowongan.getJumlah() + " orang");
        txtWaktu.setText(lowongan.getWaktu());
        txtJarak.setText(lowongan.getJarak());

        String s = lowongan.getKategori();
        String[] kat = s.split(";");

        txtKategori.setText(kat[1]);
        Drawable dw = txtKategori.getBackground();
        dw.setTint(Color.parseColor(kat[2]));

        txtDis.bringToFront();
        if (cekWaktu(lowongan.getWaktu()) < 1) {
            txtDis.setVisibility(View.VISIBLE);
            imgDis.setVisibility(View.VISIBLE);
        }
        else {
            txtDis.setVisibility(View.INVISIBLE);
            imgDis.setVisibility(View.INVISIBLE);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = itemView.getRootView().getContext();

                sp = c.getSharedPreferences("SIKBK", Context.MODE_PRIVATE);
                sp.edit().putInt("id_lowongan", lowongan.getId()).apply();

                c.startActivity(new Intent(c, DetailLowonganActivity.class));
            }
        });
    }
}