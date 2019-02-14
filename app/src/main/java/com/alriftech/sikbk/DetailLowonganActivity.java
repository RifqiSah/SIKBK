package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailLowonganActivity extends AppCompatActivity {
    Core core;

    SharedPreferences sp;
    ProgressDialog dialog;

    ImageView imgDetails;
    TextView txtBidang, txtKantor, txtDeskripsi, txtJobdesk, txtSkill, txtKnowledge, txtPersonality, txtSalary, txtJumlah, txtPersyaratan, txtWaktu;
    AppCompatButton btnLamar;

    private String low_bidang, low_deskripsi;
    private int id_lowongan, low_syar_1, low_syar_2, low_syar_3, low_syar_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lowongan);

        // Start Collapsing Action Bar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Detail Pekerjaan");
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        // End Collapsing Action Bar

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);

//        id_lowongan = getIntent().getExtras().getInt("id_lowongan");
        id_lowongan = sp.getInt("id_lowongan", 0);

        imgDetails      = findViewById(R.id.img_detail);

        txtBidang       = findViewById(R.id.txt_des_bidang);
        txtKantor       = findViewById(R.id.txt_des_kantor);
        txtDeskripsi    = findViewById(R.id.txt_des_deskripsi);
        txtJobdesk      = findViewById(R.id.txt_des_jobdesk);
        txtSkill        = findViewById(R.id.txt_des_skill);
        txtKnowledge    = findViewById(R.id.txt_des_knowledge);
        txtPersonality  = findViewById(R.id.txt_des_personality);
        txtSalary       = findViewById(R.id.txt_des_salary);
        txtJumlah       = findViewById(R.id.txt_des_jumlah);
        txtPersyaratan  = findViewById(R.id.txt_des_persyaratan);
        txtWaktu        = findViewById(R.id.txt_des_waktu);

        btnLamar        = findViewById(R.id.btn_des_lamar);

        dialog = ProgressDialog.show(this, "", "Mengambil data", true);
        new getLowonganData().execute(Integer.toString(id_lowongan), String.valueOf(sp.getInt("id_user", 0)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

    private String getPersyaratan(int _1, int _2, int _3, int _4) {
        String ret = "";

        low_syar_1 = _1;
        low_syar_2 = _2;
        low_syar_3 = _3;
        low_syar_4 = _4;

        if (_1 == 0 && _2 == 0 && _3 == 0 && _4 == 0)
            ret = "Tidak ada persyaratan";
        else {
            if (_1 == 1)
                ret = "Ijazah";

            if (_2 == 1)
                ret += ", Transkrip";

            if (_3 == 1)
                ret += ", Surat Keterangan Catatan Kepolisian";

            if (_4 == 1)
                ret += ", Surat Keterangan Sehat";
        }

        if (_1 != 1)
            ret = ret.substring(2);

        ret += " dibutuhkan.";
        return ret;
    }

    public void lamarPekerjaan(View v) {
        new AlertDialog.Builder(DetailLowonganActivity.this, R.style.Sikbk_Dialog)
                .setTitle(R.string.t_konfirmasi)
                .setMessage(R.string.b_konfirmasi_lamaran)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        Intent i = new Intent(DetailLowonganActivity.this, DaftarLowonganActivity.class);

                        i.putExtra("id_lowongan", id_lowongan);
                        i.putExtra("low_bidang", low_bidang);
                        i.putExtra("low_deskripsi", low_deskripsi);
                        i.putExtra("low_syarat_1", low_syar_1);
                        i.putExtra("low_syarat_2", low_syar_2);
                        i.putExtra("low_syarat_3", low_syar_3);
                        i.putExtra("low_syarat_4", low_syar_4);

                        startActivityForResult(i, 1);
                    }
                })
                .setNegativeButton(R.string.btn_no, null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                id_lowongan = data.getIntExtra("id_lowongan", 0);
            }
        }
    }

    private class getLowonganData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("lowongan_details/" + params[0] + "/" + params[1]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            dialog.dismiss();

            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    String url = getString(R.string.ASSETS_URL) + "lowongan/" + c.getString("lowongan_image");
                    Picasso.with(DetailLowonganActivity.this).invalidate(url);
                    Picasso.with(DetailLowonganActivity.this).load(url).into(imgDetails);
                    imgDetails.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    txtBidang.setText(c.getString("bidang")); low_bidang = c.getString("bidang");
                    txtKantor.setText("Kategori " + c.getString("nama_kategori") + " \u2022 Oleh " + c.getString("nama_kantor"));
                    txtDeskripsi.setText(c.getString("deskripsi")); low_deskripsi = c.getString("deskripsi");
                    txtJobdesk.setText(core.formatList(c.getString("jobdesk")));
                    txtSkill.setText(core.formatList(c.getString("skill")));
                    txtKnowledge.setText(core.formatList(c.getString("knowledge")));
                    txtPersonality.setText(core.formatList(c.getString("personality")));
                    txtSalary.setText(c.getString("salary"));
                    txtJumlah.setText(c.getInt("jumlah") + " orang dibutuhkan.");
                    txtPersyaratan.setText(getPersyaratan(c.getInt("ijazah"), c.getInt("transkrip"), c.getInt("skck"), c.getInt("skkb")));

                    String waktu = core.sisaWaktu(c.getString("waktu"));
                    if (jobj.getInt("self") == 1) {
                        txtWaktu.setText("Ini merupakan lowongan Anda");
                        btnLamar.setAlpha(.5f);
                        btnLamar.setClickable(false);
                    }
                    else if (waktu.equals("0")) {
                        txtWaktu.setText("Lowongan tidak tersedia!");
                        btnLamar.setAlpha(.5f);
                        btnLamar.setClickable(false);
                    }
                    else {
                        txtWaktu.setText(waktu);
                        btnLamar.setAlpha(1f);
                        btnLamar.setClickable(true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
