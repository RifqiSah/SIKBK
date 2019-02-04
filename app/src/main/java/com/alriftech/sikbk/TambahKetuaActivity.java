package com.alriftech.sikbk;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class TambahKetuaActivity extends AppCompatActivity {
    Core core;
    EditText txtRealname, txtEmail, txtUsername, txtPassword, txtNomor, txtAlamat, txtTanggalLahir;
    Spinner jk;
    ProgressDialog dialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_ketua);

        core = new Core(this);

        txtRealname     = findViewById(R.id.txtKetuaNama);
        txtEmail        = findViewById(R.id.txtKetuaEmail);
        txtUsername     = findViewById(R.id.txtKetuaUsername);
        txtPassword     = findViewById(R.id.txtKetuaPassword);
        txtNomor        = findViewById(R.id.txtKetuaNomor);
        txtAlamat       = findViewById(R.id.txtKetuaAlamat);
        txtTanggalLahir = findViewById(R.id.txtKetuaTanggalLahir);

        jk              = findViewById(R.id.spinJenisKelamin);

        txtTanggalLahir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int year = mcurrentTime.get(Calendar.YEAR);
                    int month = mcurrentTime.get(Calendar.MONTH);
                    int date = mcurrentTime.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(TambahKetuaActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            txtTanggalLahir.setText(String.format("%04d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth));
                            txtTanggalLahir.setEnabled(false);
                        }
                    }, year, month, date);

                    mDatePicker.show();
                }

                return false;
            }
        });

        ArrayList<String> jenisKelamin = new ArrayList<>();

        jenisKelamin.add("Laki-Laki");
        jenisKelamin.add("Perempuan");

        ArrayAdapter<String> adapJK = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jenisKelamin);
        adapJK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jk.setAdapter(adapJK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnucheck, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_check:
                tambahKetua();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void tambahKetua() {
        if (txtRealname.getText().toString().isEmpty()) {
            txtRealname.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtEmail.getText().toString().isEmpty()) {
            txtEmail.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtUsername.getText().toString().isEmpty()) {
            txtUsername.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtNomor.getText().toString().isEmpty()) {
            txtNomor.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtAlamat.getText().toString().isEmpty()) {
            txtAlamat.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtTanggalLahir.getText().toString().isEmpty()) {
            txtTanggalLahir.setError( "Field ini wajib diisi!" );
            return;
        }

        dialog = ProgressDialog.show(this, "", "Menyimpan data", true);
        new sendKetuaData().execute(txtRealname.getText().toString(),
                txtEmail.getText().toString(),
                txtUsername.getText().toString(),
                txtPassword.getText().toString(),
                txtNomor.getText().toString(),
                txtAlamat.getText().toString(),
                txtTanggalLahir.getText().toString(),
                jk.getSelectedItem().toString());
    }

    private class sendKetuaData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_add");
            String text = "";

            String realname         = params[0];
            String email            = params[1];
            String username         = params[2];
            String password         = params[3];
            String nomor            = params[4];
            String alamat           = params[5];
            String tanggal_lahir    = params[6];
            String jenis_kelamin    = params[7];
            String profile_image    = "default.jpg";
            String level            = "2";
            String status           = "2";

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("nomor_hp", "UTF-8") + "=" + URLEncoder.encode(nomor, "UTF-8") + "&" +
                        URLEncoder.encode("realname", "UTF-8") + "=" + URLEncoder.encode(realname, "UTF-8") + "&" +
                        URLEncoder.encode("tanggal_lahir", "UTF-8") + "=" + URLEncoder.encode(tanggal_lahir, "UTF-8") + "&" +
                        URLEncoder.encode("jenis_kelamin", "UTF-8") + "=" + URLEncoder.encode(jenis_kelamin, "UTF-8") + "&" +
                        URLEncoder.encode("alamat", "UTF-8") + "=" + URLEncoder.encode(alamat, "UTF-8") + "&" +
                        URLEncoder.encode("profile_image", "UTF-8") + "=" + URLEncoder.encode(profile_image, "UTF-8") + "&" +
                        URLEncoder.encode("level", "UTF-8") + "=" + URLEncoder.encode(level, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");

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
            dialog.dismiss();

            try {
                JSONObject obj = new JSONObject(result);
                Snackbar.make(findViewById(R.id.layout_tambah_ketua), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                if (obj.getString("status").equals("success")) {
                    txtRealname.getText().clear();
                    txtEmail.getText().clear();
                    txtUsername.getText().clear();
                    txtPassword.getText().clear();
                    txtNomor.getText().clear();
                    txtAlamat.getText().clear();
                    txtTanggalLahir.getText().clear(); txtTanggalLahir.setEnabled(true);
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
