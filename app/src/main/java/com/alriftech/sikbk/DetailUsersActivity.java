package com.alriftech.sikbk;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailUsersActivity extends AppCompatActivity {
    Core core;
    ProgressDialog dialog;
    ImageView imgUser;
    TextView txtUsername, txtEmail, txtPhone, txtRealname, txtBirthDate, txtAddress;
    ListView lstPendidikan, lstPekerjaan;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_users);

        // Start Collapsing Action Bar
        final Toolbar toolbar = findViewById(R.id.toolbar_u);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar_u);
        collapsingToolbar.setTitle("Informasi User");
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        // End Collapsing Action Bar

        core = new Core(this);

        txtUsername     = findViewById(R.id.txt_users_detail_username);
        txtEmail        = findViewById(R.id.txt_users_detail_email);
        txtPhone        = findViewById(R.id.txt_users_detail_phone);
        txtRealname     = findViewById(R.id.txt_users_detail_realname);
        txtBirthDate    = findViewById(R.id.txt_users_detail_birth_date);
        txtAddress      = findViewById(R.id.txt_users_detail_address);

        Bundle i = getIntent().getExtras();

        id_user = String.valueOf(i.getInt("id_user"));

        txtUsername.setText(i.getString("username"));
        txtEmail.setText(i.getString("email"));
        txtPhone.setText(i.getString("nomor_hp"));
        txtRealname.setText(i.getString("realname"));
        txtBirthDate.setText(i.getString("tanggal_lahir"));
        txtAddress.setText(i.getString("alamat"));

        lstPendidikan   = findViewById(R.id.lst_pendidikans);
        lstPekerjaan    = findViewById(R.id.lst_pekerjaans);

        imgUser         = findViewById(R.id.img_users);
        String url      = getString(R.string.ASSETS_URL) + "profiles/" + i.getString("profile_image");
        Picasso.with(this).invalidate(url);
        Picasso.with(this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgUser);
        imgUser.setScaleType(ImageView.ScaleType.CENTER_CROP);

        dialog = ProgressDialog.show(this, "", "Mendapatkan informasi", true);

        new getUserPendidikan().execute(id_user);
        new getUserPekerjaan().execute(id_user);
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

    private class getUserPendidikan extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_pendidikan/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                ArrayList<HashMap<String, String>> mylist = new ArrayList<>();

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("jenis", c.getString("jenis"));
                    hashMap.put("nama", c.getString("nama_pendidikan"));
                    hashMap.put("tahun", "Lulus " + c.getString("tahun_lulus"));
                    mylist.add(hashMap);
                }

                SimpleAdapter sa = new SimpleAdapter(DetailUsersActivity.this, mylist, R.layout.lst_row_pendidikan,
                        new String[] {"jenis", "nama", "tahun"}, new int[] {R.id.row_pendidikan_jenis, R.id.row_pendidikan_nama, R.id.row_pendidikan_tahun});
                lstPendidikan.setAdapter(sa);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            core.autoSizeListview(lstPendidikan, 20);
        }
    }

    private class getUserPekerjaan extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_pekerjaan/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                dialog.dismiss();

                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                ArrayList<HashMap<String, String>> mylist = new ArrayList<>();

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("jabatan", c.getString("nama_pekerjaan"));
                    hashMap.put("kantor", c.getString("instansi"));
                    hashMap.put("tahun", c.getString("tahun_dari") + " - " + c.getString("tahun_sampai"));
                    mylist.add(hashMap);
                }

                SimpleAdapter sa = new SimpleAdapter(DetailUsersActivity.this, mylist, R.layout.lst_row_pekerjaan,
                        new String[] {"jabatan", "kantor", "tahun"}, new int[] {R.id.row_pekerjaan_jabatan, R.id.row_pekerjaan_kantor, R.id.row_pekerjaan_tahun});
                lstPekerjaan.setAdapter(sa);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            core.autoSizeListview(lstPekerjaan, 20);
        }
    }
}
