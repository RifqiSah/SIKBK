package com.alriftech.sikbk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    Core core;

    SharedPreferences sp;
    TextView txt_nav_email, txt_nav_nama;
    ImageView img_nav_user;
    android.support.v7.app.AlertDialog alertDialog;

    private DrawerLayout rLay;

    private View navHeader;
    private NavigationView navView;
    private ActionBarDrawerToggle dt;

    private boolean notif;
    private boolean init_user = true;
    private boolean doubleBackToExitPressedOnce = false;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        startService(new Intent(this, LocationService.class));

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);

        rLay = findViewById(R.id.layout_dashboard);
        dt = new ActionBarDrawerToggle(this, rLay, R.string.openDrawer, R.string.closeDrawer);

        rLay.addDrawerListener(dt);
        dt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navView = findViewById(R.id.navigation_view);
        navHeader = navView.getHeaderView(0);

        txt_nav_email = navHeader.findViewById(R.id.nav_email);
        txt_nav_nama = navHeader.findViewById(R.id.nav_nama);
        img_nav_user = navHeader.findViewById(R.id.nav_pic_user);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                rLay.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.mnu_nav_beranda:
                        displaySelectedFragment(new DashboardFragment());
                        return true;

                    case R.id.mnu_nav_kantor:
                        displaySelectedFragment(new KantorFragment());
                        return true;

                    case R.id.mnu_nav_lowongan:
                        displaySelectedFragment(new LowonganFragment());
                        return true;

                    case R.id.mnu_nav_lowongan_saya:
                        displaySelectedFragment(new LowonganSayaFragment());
                        return true;

                    case R.id.mnu_nav_kategori:
                        displaySelectedFragment(new KategoriFragment());
                        return true;

                    case R.id.mnu_nav_profil:
                        startActivity(new Intent(DashboardActivity.this, UserProfileActivity.class));
                        return true;

                    case R.id.mnu_nav_anggota:
                        displaySelectedFragment(new AnggotaFragment());
                        return true;

                    case R.id.mnu_nav_anggota_akun:
                        displaySelectedFragment(new AnggotaAkunFragment());
                        return true;

                    case R.id.mnu_nav_ketua:
                        displaySelectedFragment(new KetuaFragment());
                        return true;

                    case R.id.mnu_nav_logout:
                        new AlertDialog.Builder(DashboardActivity.this, R.style.Sikbk_Dialog)
                                .setTitle(R.string.t_konfirmasi)
                                .setMessage(R.string.b_konfirmasi_keluar)
                                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int button) {
                                        signOut();
                                    }
                                })
                                .setNegativeButton(R.string.btn_no, null).show();

                        return true;

                    default:
                        Toast.makeText(getApplicationContext(),"Kesalahan Terjadi ",Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // [Google Signin]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sp.edit().clear().commit();

                        Toast.makeText(DashboardActivity.this, "Anda berhasil keluar dari sesi.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserDetail(sp.getInt("id_user", 0));

        notif = sp.getBoolean("notifikasi", false);
        sp.edit().remove("notifikasi").apply();

        if (notif) {
            navView.setCheckedItem(R.id.mnu_nav_lowongan_saya);
            displaySelectedFragment(new LowonganSayaFragment());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, LocationService.class));
    }

    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_dashboard, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (rLay.isDrawerOpen(GravityCompat.START)) {
            rLay.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.b_exitapp, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dt.onOptionsItemSelected(item))
            return true;

        return true;
    }

    private void getUserDetail(int id) {
        new getUserData().execute(String.valueOf(id));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.mnurefresh, menu);
//
//        return true;
//    }

    private void setMenu(int level) {
        Menu nav_Menu = navView.getMenu();
        init_user = false;

        switch (level) {
            case 1:
                nav_Menu.findItem(R.id.mnu_nav_beranda).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_kantor).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_lowongan).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_lowongan_saya).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_anggota).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_anggota_akun).setVisible(false);

                if (!notif) {
                    navView.setCheckedItem(R.id.mnu_nav_kategori);
                    displaySelectedFragment(new KategoriFragment());
                }

                break;

            case 2:
                nav_Menu.findItem(R.id.mnu_nav_beranda).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_kantor).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_lowongan).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_lowongan_saya).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_kategori).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_ketua).setVisible(false);

                if (!notif) {
                    navView.setCheckedItem(R.id.mnu_nav_anggota);
                    displaySelectedFragment(new AnggotaFragment());
                }

                break;

            case 3:
                nav_Menu.findItem(R.id.mnu_nav_kategori).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_anggota).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_anggota_akun).setVisible(false);
                nav_Menu.findItem(R.id.mnu_nav_ketua).setVisible(false);

                if (!notif) {
                    navView.setCheckedItem(R.id.mnu_nav_beranda);
                    displaySelectedFragment(new DashboardFragment());
                }

                break;

            case 99:
                if (!notif) {
                    navView.setCheckedItem(R.id.mnu_nav_beranda);
                    displaySelectedFragment(new DashboardFragment());
                }

                break;
        }

    }

    private String getLevel(int level) {
        if (init_user) {
            setMenu(level);

            switch (level) {
                case 1: return "Administrator";
                case 2: return "Ketua Komunitas";
                case 3:  return "Anggota";
                case 99: return "Developer";
                default: return "Tidak Diketahui";
            }
        }

        return "Tidak Diketahui";
    }

    public void tambahKantor(View v) {
        startActivity(new Intent(this, TambahKantorActivity.class));
    }

    public void tambahLowongan(View v) {
        startActivity(new Intent(this, TambahLowonganActivity.class));
    }

    public void tambahKetua(View v) {
        startActivity(new Intent(this, TambahKetuaActivity.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    public void tambahKategori(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_tambah_kategori, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText nama = promptsView.findViewById(R.id.dialog_kategori_nama);
        final EditText warna = promptsView.findViewById(R.id.dialog_kategori_warna);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                );

                                new sendKategori().execute(nama.getText().toString(), warna.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        warna.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    final ColorPicker cp = new ColorPicker(DashboardActivity.this, 0, 0, 0);
                    cp.show();

                    cp.setCallback(new ColorPickerCallback() {
                        @Override
                        public void onColorChosen(@ColorInt int color) {
                            warna.setText(String.format("#%06X", (0xFFFFFF & color)));
                            warna.setEnabled(false);

                            cp.dismiss();
                        }
                    });
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

    private class getUserData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_details/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    String url;

                    if (c.isNull("g_id") == false && !c.getString("g_profile").equals(""))
                        url = c.getString("g_profile");
                    else
                        url = getString(R.string.ASSETS_URL) + "profiles/" + c.getString("profile_image");

                    Picasso.with(DashboardActivity.this).invalidate(url);
                    Picasso.with(DashboardActivity.this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(img_nav_user);
                    img_nav_user.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    txt_nav_nama.setText(c.getString("realname") + " (" + getLevel(c.getInt("level")) + ")");
                    txt_nav_email.setText(c.getString("email"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            init_user = false;
        }
    }

    private class sendKategori extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("kategori_tambah");
            String text = "";

            String nama_kategori = params[0];
            String warna        = params[1];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("nama_kategori", "UTF-8") + "=" + URLEncoder.encode(nama_kategori, "UTF-8") + "&" +
                        URLEncoder.encode("warna", "UTF-8") + "=" + URLEncoder.encode(warna, "UTF-8");

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
                Snackbar.make(findViewById(R.id.layout_dashboard), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                if (obj.getString("status").equals("success")) {
                    if (alertDialog.isShowing())
                        alertDialog.dismiss();
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
