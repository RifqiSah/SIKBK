package com.alriftech.sikbk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Core core;

    TextView txtUname, txtPass;
    ProgressDialog dialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        core = new Core(this);

        txtUname = findViewById(R.id.txtUsername);
        txtPass = findViewById(R.id.txtPassword);

      sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
      if (sp.getBoolean("isLogin", false)) {
          gotoMain();
      }
    }

    public void registerUser(View v) {
        startActivity(new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void cekLogin(View v) {
        hideKeyboard();

        if (txtUname.getText().toString().isEmpty()) {
            txtUname.setError( "Harap isi username Anda terlebih dahulu!" );
            return;
        }

        if (txtPass.getText().toString().isEmpty()) {
            txtPass.setError( "Harap isi password Anda terlebih dahulu!" );
            return;
        }

        if (core.cekInternet(findViewById(R.id.layout_login))) {
            dialog = ProgressDialog.show(this, "", "Mencocokan informasi login", true);
            new getUserLogin().execute(txtUname.getText().toString(), txtPass.getText().toString());
        }
    }

    private void gotoMain() {
        sp.edit().putBoolean("isLogin", true).apply();

        startActivity(new Intent(getApplicationContext(), DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private class getUserLogin extends AsyncTask<String,String,String>{

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_login/" + params[0] + "/" + params[1]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            dialog.dismiss();

            try {
                JSONObject jobj = new JSONObject(json);

                if (jobj.getString("status").equals("success")) {
                    sp.edit().putInt("id_user", jobj.getInt("data")).apply();
                    gotoMain();
                } else
                    Snackbar.make(findViewById(R.id.layout_login), jobj.getString("data"), Snackbar.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
