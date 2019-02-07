package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.alriftech.sikbk.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    Core core;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
        setContentView(R.layout.activity_splash);

        startService(new Intent(this, InternetService.class));

        if (core.cekInternet())
            cekVersi();

        if (getIntent().hasExtra("notif")) {
            sp.edit().putBoolean("notifikasi", true).apply();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Cek dulu koneksi intrenetnya
                if (core.cekInternet()) {
                    cekPermission();
                } else {
                    new AlertDialog.Builder(SplashActivity.this, R.style.Sikbk_Dialog)
                            .setTitle(R.string.t_informasi)
                            .setMessage(R.string.b_no_connection)
                            .setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }
        }, 3000L);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            cekPermission();
        }
    }

    private boolean isHavePermission(String perm) {
        int result = ContextCompat.checkSelfPermission(this, perm);
//        Log.d("SikbkLog", perm + " result: " + result);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    private void setPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 101);
    }

    private void cekPermission() {
        if (!isHavePermission(android.Manifest.permission.CAMERA) ||
                !isHavePermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                !isHavePermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                !isHavePermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                !isHavePermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(SplashActivity.this, R.style.Sikbk_Dialog)
                    .setTitle(R.string.t_konfirmasi)
                    .setMessage(R.string.b_konfirmasi_permission)
                    .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            setPermission();
                        }
                    })
                    .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
        else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void cekVersi() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        new getVersion().execute(String.valueOf(versionCode), versionName);
    }

    private class getVersion extends AsyncTask<String,String,String> {

        JSONObject jobj = null;
        int verCode = 0;
        String verName = "";

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            verCode = Integer.parseInt(params[0]);
            verName = params[1];

            String url = core.API("version");
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json) {
            try {
                JSONObject jobj = new JSONObject(json);

                int vCode = jobj.getInt("versionCode");
                String vName = jobj.getString("versionName");

                Log.d("SikbkLog", "Installed version: " + verName);
                Log.d("SikbkLog", "New version: " + vName);

                TextView txtVersion = findViewById(R.id.txtVersion);
                txtVersion.setText("Version " + verName);

                if (vCode > verCode)
                    core.showNotification("Versi baru tersedia!", "Ketuk untuk melakukan pembaruan aplikasi.", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
