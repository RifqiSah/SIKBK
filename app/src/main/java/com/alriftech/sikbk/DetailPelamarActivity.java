package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DetailPelamarActivity extends AppCompatActivity {
    Core core;
    SharedPreferences sp;
    String id_pelamar, id_user;
    TextView txtBidang, txtPelamar, txtPesan, txtBalasan;
    AppCompatButton btnTerima, btnTolak;

    String str_Syarat1, str_Syarat2, str_Syarat3, str_Syarat4;
    ImageView img_syarat_1, img_syarat_2, img_syarat_3, img_syarat_4;
    TextView txt_syarat_1, txt_syarat_2, txt_syarat_3, txt_syarat_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pelamar);

        Bundle i = getIntent().getExtras();
        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);

        img_syarat_1    = findViewById(R.id.img_lamar_syarat_1);
        img_syarat_2    = findViewById(R.id.img_lamar_syarat_2);
        img_syarat_3    = findViewById(R.id.img_lamar_syarat_3);
        img_syarat_4    = findViewById(R.id.img_lamar_syarat_4);

        txt_syarat_1    = findViewById(R.id.txt_lamar_syarat_1);
        txt_syarat_2    = findViewById(R.id.txt_lamar_syarat_2);
        txt_syarat_3    = findViewById(R.id.txt_lamar_syarat_3);
        txt_syarat_4    = findViewById(R.id.txt_lamar_syarat_4);

        txtBidang   = findViewById(R.id.txt_pelamlih_bidang);
        txtPelamar  = findViewById(R.id.txt_pelamlih_nama);
        txtPesan    = findViewById(R.id.txt_pelamlih_pesan);
        txtBalasan  = findViewById(R.id.txt_pelamlih_balasan);

        btnTerima   = findViewById(R.id.btn_terima);
        btnTolak    = findViewById(R.id.btn_tolak);

        txtBidang.setText(sp.getString("low_bidang", ""));

        if (savedInstanceState != null) {
            txtPelamar.setText(savedInstanceState.getString("nama"));

            id_pelamar = savedInstanceState.getString("id_pelamar");
            id_user = savedInstanceState.getString("id_user");
        }
        else {
            txtPelamar.setText(i.getString("nama"));

            id_pelamar = i.getString("id_pelamar");
            id_user = i.getString("id_user");
        }

        new getPelamarDetailsData().execute(id_pelamar);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        txtPelamar.setText(savedInstanceState.getString("nama"));
        id_pelamar = savedInstanceState.getString("id_pelamar");
        id_user = savedInstanceState.getString("id_user");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("nama", txtPelamar.getText().toString());
        outState.putString("id_pelamar", id_pelamar);
        outState.putString("id_user", id_user);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
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

    private void setDokumen(int id, String nilai) {
        Log.d("SikbkLog", getString(R.string.ASSETS_URL) + "documents/" + nilai);

        if (nilai.length() > 0) {
            nilai = nilai.trim();

            switch (id) {
                case 1:
                    img_syarat_1.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_1.setTextColor(getColor(R.color.colorAccent));

                    str_Syarat1 = nilai;
                    break;

                case 2:
                    img_syarat_2.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_2.setTextColor(getColor(R.color.colorAccent));

                    str_Syarat2 = nilai;
                    break;

                case 3:
                    img_syarat_3.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_3.setTextColor(getColor(R.color.colorAccent));

                    str_Syarat3 = nilai;
                    break;

                case 4:
                    img_syarat_4.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_4.setTextColor(getColor(R.color.colorAccent));

                    str_Syarat4 = nilai;
                    break;
            }
        }
    }

    public void docUser1(View v) {
        if (!core.isNullOrEmpty(str_Syarat1))
            new DownloadFile().execute(getString(R.string.ASSETS_URL) + "documents/" + str_Syarat1);
    }

    public void docUser2(View v) {
        if (!core.isNullOrEmpty(str_Syarat2))
            new DownloadFile().execute(getString(R.string.ASSETS_URL) + "documents/" + str_Syarat2);
    }

    public void docUser3(View v) {
        if (!core.isNullOrEmpty(str_Syarat3))
            new DownloadFile().execute(getString(R.string.ASSETS_URL) + "documents/" + str_Syarat3);
    }

    public void docUser4(View v) {
        if (!core.isNullOrEmpty(str_Syarat4))
            new DownloadFile().execute(getString(R.string.ASSETS_URL) + "documents/" + str_Syarat4);
    }

    public void lihatUser(View v) {
        Intent i = new Intent(DetailPelamarActivity.this, DetailPelamarUserActivity.class);
        i.putExtra("id_user", id_user);
        startActivity(i);
    }

    private void lihatPdf(final String path) {
        File file = new File(path);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null)
            type = "*/*";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);

        intent.setDataAndType(data, type);
        startActivity(intent);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new File(path).getAbsoluteFile().delete();
            }
        }, 5000L);
    }

    private class DownloadFile extends AsyncTask<String, String, String> {
        private static final int MEGABYTE = 1024 * 1024;

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(DetailPelamarActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                fileName = timestamp + "_" + fileName;
                folder = Environment.getExternalStorageDirectory() + File.separator + "SIKBK/";

                File directory = new File(folder);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Log.d("SikbkLog", "Content length: " + lengthOfFile);

                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[MEGABYTE];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

                return folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Terjadi kesalahan saat mengunduh berkas!";
        }

        protected void onProgressUpdate(String... progress) {
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            this.progressDialog.dismiss();

            if (message.substring(0, 1).equals("/"))
                lihatPdf(message);
            else
                Snackbar.make(findViewById(R.id.layout_detail_pelamar), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public void pelamar_terima(View v) {
        proses_pelamar(1);
    }

    public void pelamar_tolak(View v) {
        proses_pelamar(2);
    }

    private void proses_pelamar(final int status) {
        new AlertDialog.Builder(DetailPelamarActivity.this, R.style.Sikbk_Dialog)
                .setTitle(R.string.t_konfirmasi)
                .setMessage((status == 1 ? R.string.b_yakin_terima : R.string.b_yakin_tolak))
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        new sendKonfirmasi().execute(id_pelamar, String.valueOf(status), txtBalasan.getText().toString());
                    }
                })
                .setNegativeButton(R.string.btn_no, null).show();
    }

    private class getPelamarDetailsData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("pelamar_details/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    txtPesan.setText(c.getString("pesan"));

                    setDokumen(1, c.getString("ijazah"));
                    setDokumen(2, c.getString("transkrip"));
                    setDokumen(3, c.getString("skck"));
                    setDokumen(4, c.getString("skkb"));

                    if (c.getInt("status") != 3) {
                        btnTerima.setEnabled(false); btnTerima.setBackgroundColor(getColor(R.color.gray));
                        btnTolak.setEnabled(false); btnTolak.setBackgroundColor(getColor(R.color.gray));

                        txtBalasan.setText(c.getString("balasan"));
                        txtBalasan.setEnabled(false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class sendKonfirmasi extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("pelamar_konfirmasi/" + params[0] + "/" + params[1]);
            String text = "";

            String balasan      = params[2];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("balasan", "UTF-8") + "=" + URLEncoder.encode(balasan, "UTF-8");

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();

                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        sb.append(line).append("\n");

                    text = sb.toString();
                    bufferedWriter.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                Snackbar.make(findViewById(R.id.layout_detail_pelamar), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                if (obj.getString("status").equals("success")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
