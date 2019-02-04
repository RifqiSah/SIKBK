package com.alriftech.sikbk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LowonganSayaHolder extends RecyclerView.ViewHolder {

    private TextView txtBidang, txtKantor, txtDeskripsi, txtDibuatPada, txtStatus;

    public LowonganSayaHolder(View itemView) {
        super(itemView);

        this.txtBidang      = itemView.findViewById(R.id.txt_list_lowongan_saya_bidang);
        this.txtKantor      = itemView.findViewById(R.id.txt_list_lowongan_saya_kantor);
        this.txtDibuatPada  = itemView.findViewById(R.id.txt_list_lowongan_saya_waktu);
        this.txtDeskripsi   = itemView.findViewById(R.id.txt_list_lowongan_saya_deskripsi);
        this.txtStatus      = itemView.findViewById(R.id.txt_list_lowongan_saya_status);
    }

    private void setStatus(int kode) {
        switch (kode) {
            case 1:
                txtStatus.setText("Diterima");
                txtStatus.setBackgroundResource(R.drawable.kategori_layout_7);
                break;

            case 2:
                txtStatus.setText("Ditolak");
                txtStatus.setBackgroundResource(R.drawable.kategori_layout_2);
                break;

            case 3:
                txtStatus.setText("Menunggu");
                txtStatus.setBackgroundResource(R.drawable.kategori_layout_default);
                break;
        }
    }

    public void setDetails(final LowonganSaya lowonganSaya) {
        txtBidang.setText(lowonganSaya.getBidang());
        txtKantor.setText(lowonganSaya.getKantor());
        txtDibuatPada.setText(lowonganSaya.getDibuat_pada());
        txtDeskripsi.setText(lowonganSaya.getDeskripsi());

        setStatus(Integer.valueOf(lowonganSaya.getStatus()));
    }
}
