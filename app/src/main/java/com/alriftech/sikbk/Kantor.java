package com.alriftech.sikbk;

public class Kantor {
    private int id_user;
    private int id_kantor;
    private double latitude;
    private double longitude;
    private String nama_kantor;
    private String jam_buka;
    private String jam_tutup;
    private String alamat;

    public Kantor(int id_user, int id_kantor, double latitude, double longitude, String nama_kantor, String jam_buka, String jam_tutup, String alamat) {
        this.id_user = id_user;
        this.id_kantor = id_kantor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nama_kantor = nama_kantor;
        this.jam_buka = jam_buka;
        this.jam_tutup = jam_tutup;
        this.alamat = alamat;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_kantor() {
        return id_kantor;
    }

    public void setId_kantor(int id_kantor) {
        this.id_kantor = id_kantor;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNama_kantor() {
        return nama_kantor;
    }

    public void setNama_kantor(String nama_kantor) {
        this.nama_kantor = nama_kantor;
    }

    public String getJam_buka() {
        return jam_buka;
    }

    public void setJam_buka(String jam_buka) {
        this.jam_buka = jam_buka;
    }

    public String getJam_tutup() {
        return jam_tutup;
    }

    public void setJam_tutup(String jam_tutup) {
        this.jam_tutup = jam_tutup;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
