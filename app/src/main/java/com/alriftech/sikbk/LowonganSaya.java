package com.alriftech.sikbk;

public class LowonganSaya {
    private int id_pelamar;
    private String bidang;
    private String deskripsi;
    private String kantor;
    private String dibuat_pada;
    private String status;
    private String balasan;

    public LowonganSaya(int id_pelamar, String bidang, String deskripsi, String kantor, String dibuat_pada, String status, String balasan) {
        this.id_pelamar = id_pelamar;
        this.bidang = bidang;
        this.deskripsi = deskripsi;
        this.kantor = kantor;
        this.dibuat_pada = dibuat_pada;
        this.status = status;
        this.balasan = balasan;
    }

    public int getId_pelamar() {
        return id_pelamar;
    }

    public void setId_pelamar(int id_pelamar) {
        this.id_pelamar = id_pelamar;
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

    public String getDibuat_pada() {
        return dibuat_pada;
    }

    public void setDibuat_pada(String dibuat_pada) {
        this.dibuat_pada = dibuat_pada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBalasan() {
        return balasan;
    }

    public void setBalasan(String balasan) {
        this.balasan = balasan;
    }
}
