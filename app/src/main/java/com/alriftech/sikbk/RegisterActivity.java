package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {
    Core core;

    TextView txtUname, txtPass, txtEmail, txtRealname;
    String sName, sPass, sEmail, sRealname;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        core = new Core(this);

        txtUname = findViewById(R.id.txtRegUsername);
        txtPass = findViewById(R.id.txtRegPassword);
        txtEmail = findViewById(R.id.txtRegEmail);
        txtRealname = findViewById(R.id.txtRegRealname);
    }

    public void loginUser(View v) {
        startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void cekRegister(View v) {
        hideKeyboard();

        sName = txtUname.getText().toString();
        sPass = txtPass.getText().toString();
        sEmail = txtEmail.getText().toString();
        sRealname = txtRealname.getText().toString();

        if (sName.isEmpty()) {
            txtUname.setError( "Harap isi username Anda!" );
            return;
        }

        if (sPass.isEmpty() || sPass.length() < 4 || sPass.length() > 11) {
            txtPass.setError( "Kata sandi minimal terdiri dari 4 sampai 12 karakter!" );
            return;
        }

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            txtEmail.setError( "Harap isi email yang benar!" );
            return;
        }

        if (sRealname.isEmpty()) {
            txtPass.setError( "Harap isi nama lengkap Anda!" );
            return;
        }

        new AlertDialog.Builder(this, R.style.Sikbk_Dialog)
                .setTitle(R.string.t_konfirmasi)
                .setMessage(R.string.b_konfirmasi_registrasi)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        _register();
                    }
                })
                .setNegativeButton(R.string.btn_no, null).show();
    }

    private void _register() {
        if (core.cekInternet(findViewById(R.id.layout_register))) {
            dialog = ProgressDialog.show(this, "", "Mendaftarkan akun Anda", true);
            new sendUserData().execute(sName, sPass, sEmail, sRealname);
        }
    }

    private class sendUserData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_register");
            String text = "";

            String username = params[0];
            String password = params[1];
            String email = params[2];
            String realname = params[3];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                              URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                              URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                              URLEncoder.encode("realname", "UTF-8") + "=" + URLEncoder.encode(realname, "UTF-8");

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
            Log.d("SikbkLog", result);

            try {
                JSONObject obj = new JSONObject(result);

                dialog.dismiss();
                Snackbar.make(findViewById(R.id.layout_register), obj.getString("data"), Snackbar.LENGTH_LONG).show();

//                if (obj.getString("status").equals("success"))
//                    loginUser(null);
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