package com.alriftech.sikbk;

public class Users {
    private int id;
    private String username;
    private String email;
    private String nomor_hp;
    private String realname;
    private String tanggal_lahir;
    private String jenis_kelamin;
    private String alamat;
    private int level;
    private int status;
    private boolean anggota;
    private String profile_image;

    public Users(int id, String username, String email, String nomor_hp, String realname, String tanggal_lahir, String jenis_kelamin, String alamat, int level, int status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nomor_hp = nomor_hp;
        this.realname = realname;
        this.tanggal_lahir = tanggal_lahir;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.level = level;
        this.status = status;
    }

    public Users(int id, String username, String email, String nomor_hp, String realname, String tanggal_lahir, String jenis_kelamin, String alamat, int level, int status, boolean anggota, String profile_image) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nomor_hp = nomor_hp;
        this.realname = realname;
        this.tanggal_lahir = tanggal_lahir;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.level = level;
        this.status = status;
        this.anggota = anggota;
        this.profile_image = profile_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomor_hp() {
        return nomor_hp;
    }

    public void setNomor_hp(String nomor_hp) {
        this.nomor_hp = nomor_hp;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAnggota() {
        return anggota;
    }

    public void setAnggota(boolean anggota) {
        this.anggota = anggota;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
