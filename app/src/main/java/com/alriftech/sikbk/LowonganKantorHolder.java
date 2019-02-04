package com.alriftech.sikbk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LowonganKantorHolder extends RecyclerView.ViewHolder {
    SharedPreferences sp;
    private TextView txtBidang, txtKantor, txtDeskripsi, txtKategori;

    public LowonganKantorHolder(View itemView) {
        super(itemView);

        this.txtBidang      = itemView.findViewById(R.id.txt_list_lowongan_bidang);
        this.txtKantor      = itemView.findViewById(R.id.txt_list_lowongan_kantor);
        this.txtDeskripsi   = itemView.findViewById(R.id.txt_list_lowongan_deskripsi);
        this.txtKategori    = itemView.findViewById(R.id.txt_list_lowongan_kategori);
    }

    public void setDetails(final LowonganKantor lowonganKantor) {
        txtBidang.setText(lowonganKantor.getBidang());
        txtKantor.setText(lowonganKantor.getKantor());
        txtDeskripsi.setText(lowonganKantor.getDeskripsi());

        String s = lowonganKantor.getKategori();
        String[] kat = s.split(";");

        txtKategori.setText(kat[1]);
        Drawable dw = txtKategori.getBackground();
        dw.setTint(Color.parseColor(kat[2]));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = itemView.getRootView().getContext();

                sp = c.getSharedPreferences("SIKBK", Context.MODE_PRIVATE);
                sp.edit().putString("low_id", String.valueOf(lowonganKantor.getId_lowongan())).apply();
                sp.edit().putString("low_bidang", lowonganKantor.getBidang()).apply();
                sp.edit().putString("low_deskripsi", lowonganKantor.getDeskripsi()).apply();

                c.startActivity(new Intent(c, DetailLowonganKantorActivity.class));
            }
        });
    }
}
