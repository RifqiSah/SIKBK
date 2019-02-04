package com.alriftech.sikbk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Locale;

public class TambahKantorActivity extends AppCompatActivity {

    Core core;
    SharedPreferences sp;
    ProgressDialog dialog;
    String id_user;
    EditText txtNamaKantor, txtJamBuka, txtJamTutup, txtLokasiKantor, txtAlamat;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kantor);

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));

        txtNamaKantor   = findViewById(R.id.txtNamaKantor);
        txtJamBuka      = findViewById(R.id.txtJamBuka);
        txtJamTutup     = findViewById(R.id.txtJamTutup);
        txtLokasiKantor = findViewById(R.id.txtLokasiKantor);
        txtAlamat       = findViewById(R.id.txtAlamatKantor);

        txtJamBuka.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(TambahKantorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            txtJamBuka.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                            txtJamBuka.setEnabled(false);
                        }
                    }, hour, minute, true);

                    mTimePicker.show();
                }

                return false;
            }
        });

        txtJamTutup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(TambahKantorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            txtJamTutup.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                            txtJamTutup.setEnabled(false);
                        }
                    }, hour, minute, true);

                    mTimePicker.show();
                }

                return false;
            }
        });

        txtLokasiKantor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    startActivityForResult(new Intent(TambahKantorActivity.this, MapActivity.class), 100);
                }

                return false;
            }
        });
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String latlong = data.getStringExtra("select_lat") + ", " + data.getStringExtra("select_long");
                txtLokasiKantor.setText(latlong);
                txtLokasiKantor.setEnabled(false);
            }
        }
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
                tambahKantor();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tambahKantor() {
        if (txtNamaKantor.getText().toString().isEmpty()) {
            txtNamaKantor.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtJamBuka.getText().toString().isEmpty()) {
            txtJamBuka.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtJamTutup.getText().toString().isEmpty()) {
            txtJamTutup.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtLokasiKantor.getText().toString().isEmpty()) {
            txtLokasiKantor.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtAlamat.getText().toString().isEmpty()) {
            txtAlamat.setError( "Field ini wajib diisi!" );
            return;
        }
        dialog = ProgressDialog.show(this, "", "Menyimpan data", true);
        new sendKantorData().execute(id_user,
                txtLokasiKantor.getText().toString(),
                txtJamBuka.getText().toString(),
                txtJamTutup.getText().toString(),
                txtAlamat.getText().toString(),
                txtNamaKantor.getText().toString());
    }

    private class sendKantorData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_kantor_tambah");
            String text = "";

            String id_user      = params[0];
            String latlang      = params[1];
            String jam_buka     = params[2];
            String jam_tutup    = params[3];
            String alamat       = params[4];
            String nama_kantor  = params[5];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("id_user", "UTF-8") + "=" + URLEncoder.encode(id_user, "UTF-8") + "&" +
                        URLEncoder.encode("latlang", "UTF-8") + "=" + URLEncoder.encode(latlang, "UTF-8") + "&" +
                        URLEncoder.encode("jam_buka", "UTF-8") + "=" + URLEncoder.encode(jam_buka, "UTF-8") + "&" +
                        URLEncoder.encode("jam_tutup", "UTF-8") + "=" + URLEncoder.encode(jam_tutup, "UTF-8") + "&" +
                        URLEncoder.encode("alamat", "UTF-8") + "=" + URLEncoder.encode(alamat, "UTF-8") + "&" +
                        URLEncoder.encode("nama_kantor", "UTF-8") + "=" + URLEncoder.encode(nama_kantor, "UTF-8");

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
                Snackbar.make(findViewById(R.id.layout_tambah_kantor), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                if (obj.getString("status").equals("success")) {
                    txtNamaKantor.getText().clear();
                    txtJamBuka.getText().clear(); txtJamBuka.setEnabled(true);
                    txtJamTutup.getText().clear(); txtJamTutup.setEnabled(true);
                    txtLokasiKantor.getText().clear(); txtLokasiKantor.setEnabled(true);
                    txtAlamat.getText().clear();
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
