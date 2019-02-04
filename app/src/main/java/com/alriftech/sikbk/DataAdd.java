package com.alriftech.sikbk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DataAdd extends AppCompatActivity {
    Integer id_user = -1;
    JSONParser jsonparser = new JSONParser();
    EditText txtLat, txtLong, txtNama, txtPemilik, txtAwal, txtAkhir, txtWebsite, txtNomor;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_add);

        txtLat = (EditText)findViewById(R.id.txtLokLatitude);
        txtLong = (EditText)findViewById(R.id.txtLokLongitude);
        txtNama = (EditText)findViewById(R.id.txtLokNama);
        txtPemilik = (EditText)findViewById(R.id.txtLokPemilik);
        txtAwal = (EditText)findViewById(R.id.txtLokAwal);
        txtAkhir = (EditText)findViewById(R.id.txtLokAkhir);
        txtWebsite = (EditText)findViewById(R.id.txtLokWebsite);
        txtNomor = (EditText)findViewById(R.id.txtLokNomor);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        id_user = getIntent().getExtras().getInt("id_user");
    }

    public void tambahLokasi(View v) {
        if (txtLat.getText().toString().trim().equals("")) {
            txtLat.setError( "Harap isi Latitude dari lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtLong.getText().toString().trim().equals("")) {
            txtLong.setError( "Harap isi Longitude dari lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtNama.getText().toString().trim().equals("")) {
            txtNama.setError( "Harap isi Nama dari lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtPemilik.getText().toString().trim().equals("")) {
            txtPemilik.setError( "Harap isi Pemilik dari lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtAwal.getText().toString().trim().equals("")) {
            txtAwal.setError( "Harap isi waktu awal lokasi kerja dibuka lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtAkhir.getText().toString().trim().equals("")) {
            txtAkhir.setError( "Harap isi waktu akhir lokasi kerja dibuka lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtWebsite.getText().toString().trim().equals("")) {
            txtWebsite.setError( "Harap isi Website dari lokasi tersebut terlebih dahulu!" );
            return;
        }

        if (txtNomor.getText().toString().trim().equals("")) {
            txtNomor.setError( "Harap isi Nomor Telepon dari lokasi tersebut terlebih dahulu!" );
            return;
        }

//        String url = "https://www.alriftech.com/api/apps/markeradd/" + txtLat.getText() + "/" + txtLong.getText() + "/" + txtNama.getText() + "/" + txtPemilik.getText() + "/" + txtAwal.getText() + " - " + txtAkhir.getText() + "/" + txtWebsite.getText() + "/" + txtNomor.getText() + "/" + Integer.toString(id_user);
//        url = url.replace(" ", "%20");
//        Toast.makeText(DataAdd.this, url, Toast.LENGTH_SHORT).show();
        dialog = ProgressDialog.show(this, "", "Menyimpan data ...", true);
        new senddata().execute();
    }

    class senddata extends AsyncTask<String,String,String> {

        JSONObject jobj = null;
        String a = null;
        String b = null;

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub

            String url = "https://www.alriftech.com/api/apps/markeradd/" + txtLat.getText() + "/" + txtLong.getText() + "/" + txtNama.getText() + "/" + txtPemilik.getText() + "/" + txtAwal.getText() + " - " + txtAkhir.getText() + "/" + txtWebsite.getText() + "/" + txtNomor.getText() + "/" + Integer.toString(id_user);
            url = url.replace(" ", "%20");
            jobj = jsonparser.makeHttpRequest(url);

            try {
                a = jobj.getString("status");
                b = jobj.getString("data");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return a + "," + b;
        }

        protected void onPostExecute(String ab){
            dialog.dismiss();

            String[] items = ab.split(",");

            if (items[0].equals("success")) {
                Toast.makeText(DataAdd.this, items[1], Toast.LENGTH_SHORT).show();
            }
        }
    }
}
