package com.alriftech.sikbk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    Core core;

    TextView txtUname, txtPass;
    ProgressDialog dialog;
    SharedPreferences sp;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

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

        // [Google Signin]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SikbkLog", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void signIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            if (core.cekInternet(findViewById(R.id.layout_login))) {
                dialog = ProgressDialog.show(this, "", "Mencocokan informasi login", true);
                new getUserLoginGoogle().execute(account.getId());
            }
            // Log.d("SikbkLog", account.getPhotoUrl().toString());

//            Toast.makeText(this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
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

            stopService(new Intent(LoginActivity.this, InternetService.class));
            startService(new Intent(LoginActivity.this, InternetService.class));
        }
    }

    private class getUserLoginGoogle extends AsyncTask<String,String,String>{

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_login_google/" + params[0]);
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
                } else {
                    Snackbar.make(findViewById(R.id.layout_login), jobj.getString("data"), Snackbar.LENGTH_LONG).show();
                    mGoogleSignInClient.signOut();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            stopService(new Intent(LoginActivity.this, InternetService.class));
            startService(new Intent(LoginActivity.this, InternetService.class));
        }
    }
}
