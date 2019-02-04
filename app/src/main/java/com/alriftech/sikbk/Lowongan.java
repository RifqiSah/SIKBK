package com.alriftech.sikbk;

public class Lowongan {
    private int id;
    private String bidang;
    private String kantor;
    private String deskripsi;
    private int jumlah;
    private String waktu;
    private String kategori;
    private String jarak;

    public Lowongan(int id, String bidang, String kantor, String deskripsi, int jumlah, String waktu, String kategori, String jarak) {
        this.id = id;
        this.bidang = bidang;
        this.kantor = kantor;
        this.deskripsi = deskripsi;
        this.jumlah = jumlah;
        this.waktu = waktu;
        this.kategori = kategori;
        this.jarak = jarak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getKantor() {
        return kantor;
    }

    public void setKantor(String kantor) {
        this.kantor = kantor;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }
}
