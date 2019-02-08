package com.alriftech.sikbk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.List;

public class TambahLowonganActivity extends AppCompatActivity {

    Core core;
    SharedPreferences sp;
    String id_user;
    ProgressDialog dialog;

    EditText txtLowonganBidang, txtLowonganDeskripsi, txtLowonganSampai, txtLowonganSampaiWaktu, txtJobdesk, txtSkill, txtKnowledge, txtPersonality, txtSalary;
    Spinner spinKategori, spinKantor, spinJumlah;
    CheckBox chkS1, chkS2, chkS3, chkS4;
    ArrayList<String> kategori_list, kantor_list, jumlah_list;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_lowongan);

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));

        new getKategoriData().execute();
        new getKantorData().execute(id_user);

        txtLowonganBidang       = findViewById(R.id.txtLowonganBidang);
        txtLowonganDeskripsi    = findViewById(R.id.txtLowonganDeskripsi);
        txtLowonganSampai       = findViewById(R.id.txtLowonganSampai);
        txtLowonganSampaiWaktu  = findViewById(R.id.txtLowonganSampaiWaktu);

        spinKantor              = findViewById(R.id.spinLowonganKantor);
        spinKategori            = findViewById(R.id.spinLowonganKategori);
        spinJumlah              = findViewById(R.id.spinLowonganJumlah);

        chkS1                   = findViewById(R.id.chkSyarat1);
        chkS2                   = findViewById(R.id.chkSyarat2);
        chkS3                   = findViewById(R.id.chkSyarat3);
        chkS4                   = findViewById(R.id.chkSyarat4);

        txtJobdesk              = findViewById(R.id.txtLowonganJobdesk);
        txtSkill                = findViewById(R.id.txtLowonganSkill);
        txtKnowledge            = findViewById(R.id.txtLowonganKnowledge);
        txtPersonality          = findViewById(R.id.txtLowonganPersonality);
        txtSalary               = findViewById(R.id.txtLowonganSalary);

        txtLowonganSampai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int year = mcurrentTime.get(Calendar.YEAR);
                    int month = mcurrentTime.get(Calendar.MONTH);
                    int date = mcurrentTime.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(TambahLowonganActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            txtLowonganSampai.setText(String.format("%04d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth));
                            txtLowonganSampai.setEnabled(false);
                        }
                    }, year, month, date);

                    mDatePicker.show();
                }

                return false;
            }
        });

        txtLowonganSampaiWaktu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(TambahLowonganActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            txtLowonganSampaiWaktu.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                            txtLowonganSampaiWaktu.setEnabled(false);
                        }
                    }, hour, minute, true);

                    mTimePicker.show();
                }

                return false;
            }
        });

        jumlah_list = new ArrayList<>();

        for (int i = 1; i <= 10; i++)
            jumlah_list.add(String.valueOf(i) + " orang");

        ArrayAdapter<String> adapJumlah = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jumlah_list);
        adapJumlah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinJumlah.setAdapter(adapJumlah);
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                tambahLowongan();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tambahLowongan() {
        if (txtLowonganBidang.getText().toString().isEmpty()) {
            txtLowonganBidang.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtLowonganDeskripsi.getText().toString().isEmpty()) {
            txtLowonganDeskripsi.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtLowonganSampai.getText().toString().isEmpty()) {
            txtLowonganSampai.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtLowonganSampaiWaktu.getText().toString().isEmpty()) {
            txtLowonganSampaiWaktu.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtJobdesk.getText().toString().isEmpty()) {
            txtJobdesk.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtSkill.getText().toString().isEmpty()) {
            txtSkill.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtKnowledge.getText().toString().isEmpty()) {
            txtKnowledge.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtPersonality.getText().toString().isEmpty()) {
            txtPersonality.setError( "Field ini wajib diisi!" );
            return;
        }

        if (txtSalary.getText().toString().isEmpty()) {
            txtSalary.setError( "Field ini wajib diisi!" );
            return;
        }

        dialog = ProgressDialog.show(this, "", "Menyimpan data", true);
        new sendLowonganData().execute(spinKantor.getSelectedItem().toString(),
                txtLowonganSampai.getText().toString() + " " + txtLowonganSampaiWaktu.getText().toString(),
                txtLowonganBidang.getText().toString(),
                String.valueOf(spinKategori.getSelectedItemPosition() + 1),
                String.valueOf(spinJumlah.getSelectedItemPosition() + 1),
                String.valueOf((chkS1.isChecked() ? 1 : 0)),
                String.valueOf((chkS2.isChecked() ? 1 : 0)),
                String.valueOf((chkS3.isChecked() ? 1 : 0)),
                String.valueOf((chkS4.isChecked() ? 1 : 0)),
                txtLowonganDeskripsi.getText().toString(),
                txtJobdesk.getText().toString(),
                txtSkill.getText().toString(),
                txtKnowledge.getText().toString(),
                txtPersonality.getText().toString(),
                txtSalary.getText().toString());
    }

    private class getKategoriData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("kategori");
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json) {
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    kategori_list.add(c.getString("nama_kategori"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapKategori = new ArrayAdapter<>(TambahLowonganActivity.this, android.R.layout.simple_spinner_item, kategori_list);
            adapKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinKategori.setAdapter(adapKategori);
        }

        @Override
        protected void onPreExecute() {
            kategori_list = new ArrayList<>();
        }
    }

    private class getKantorData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_kantor/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json) {
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    kantor_list.add(c.getString("nama_kantor"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapKantor = new ArrayAdapter<>(TambahLowonganActivity.this, android.R.layout.simple_spinner_item, kantor_list);
            adapKantor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinKantor.setAdapter(adapKantor);

            // Proteksi
            if (kantor_list.size() <= 0) {
                hideKeyboard();

                new AlertDialog.Builder(TambahLowonganActivity.this, R.style.Sikbk_Dialog)
                        .setTitle(R.string.t_informasi)
                        .setMessage(R.string.b_infokantorkosong)
                        .setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
            }
        }

        @Override
        protected void onPreExecute() {
            kantor_list = new ArrayList<>();
        }
    }

    private class sendLowonganData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_lowongan_tambah");
            String text = "";

            String kantor       = params[0];
            String waktu        = params[1];
            String bidang       = params[2];
            String id_kategori  = params[3];
            String jumlah       = params[4];
            String ijazah       = params[5];
            String transkrip    = params[6];
            String skck         = params[7];
            String skkb         = params[8];
            String deskripsi    = params[9];
            String jobdesk      = params[10];
            String skill        = params[11];
            String knowledge    = params[12];
            String personality  = params[13];
            String salary       = params[14];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("kantor", "UTF-8") + "=" + URLEncoder.encode(kantor, "UTF-8") + "&" +
                        URLEncoder.encode("waktu", "UTF-8") + "=" + URLEncoder.encode(waktu, "UTF-8") + "&" +
                        URLEncoder.encode("bidang", "UTF-8") + "=" + URLEncoder.encode(bidang, "UTF-8") + "&" +
                        URLEncoder.encode("id_kategori", "UTF-8") + "=" + URLEncoder.encode(id_kategori, "UTF-8") + "&" +
                        URLEncoder.encode("jumlah", "UTF-8") + "=" + URLEncoder.encode(jumlah, "UTF-8") + "&" +
                        URLEncoder.encode("ijazah", "UTF-8") + "=" + URLEncoder.encode(ijazah, "UTF-8") + "&" +
                        URLEncoder.encode("transkrip", "UTF-8") + "=" + URLEncoder.encode(transkrip, "UTF-8") + "&" +
                        URLEncoder.encode("skck", "UTF-8") + "=" + URLEncoder.encode(skck, "UTF-8") + "&" +
                        URLEncoder.encode("skkb", "UTF-8") + "=" + URLEncoder.encode(skkb, "UTF-8") + "&" +
                        URLEncoder.encode("deskripsi", "UTF-8") + "=" + URLEncoder.encode(deskripsi, "UTF-8") + "&" +
                        URLEncoder.encode("jobdesk", "UTF-8") + "=" + URLEncoder.encode(jobdesk, "UTF-8") + "&" +
                        URLEncoder.encode("skill", "UTF-8") + "=" + URLEncoder.encode(skill, "UTF-8") + "&" +
                        URLEncoder.encode("knowledge", "UTF-8") + "=" + URLEncoder.encode(knowledge, "UTF-8") + "&" +
                        URLEncoder.encode("personality", "UTF-8") + "=" + URLEncoder.encode(personality, "UTF-8") + "&" +
                        URLEncoder.encode("salary", "UTF-8") + "=" + URLEncoder.encode(salary, "UTF-8");

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
                Snackbar.make(findViewById(R.id.layout_tambah_lowongan), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                if (obj.getString("status").equals("success")) {
                    txtLowonganBidang.getText().clear();
                    txtLowonganDeskripsi.getText().clear();
                    txtLowonganSampai.getText().clear(); txtLowonganSampai.setEnabled(true);
                    txtLowonganSampaiWaktu.getText().clear(); txtLowonganSampaiWaktu.setEnabled(true);
                    txtJobdesk.getText().clear();
                    txtSkill.getText().clear();
                    txtKnowledge.getText().clear();
                    txtPersonality.getText().clear();
                    txtSalary.getText().clear();
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