package com.alriftech.sikbk;

public class Kategori {
    private int id;
    private String nama_kategori;
    private String warna;

    public Kategori(int id, String nama_kategori, String warna) {
        this.id = id;
        this.nama_kategori = nama_kategori;
        this.warna = warna;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public void setNama_kategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }
}
