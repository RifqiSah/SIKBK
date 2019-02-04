package com.alriftech.sikbk;

public class LowonganKantor {
    private int id_lowongan;
    private String bidang;
    private String kategori;
    private int jumlah;
    private String deskripsi;
    private String kantor;

    public LowonganKantor(int id_lowongan, String bidang, String kategori, int jumlah, String deskripsi, String kantor) {
        this.id_lowongan = id_lowongan;
        this.bidang = bidang;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.kantor = kantor;
    }

    public int getId_lowongan() {
        return id_lowongan;
    }

    public void setId_lowongan(int id_lowongan) {
        this.id_lowongan = id_lowongan;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKantor() {
        return kantor;
    }

    public void setKantor(String kantor) {
        this.kantor = kantor;
    }
}
