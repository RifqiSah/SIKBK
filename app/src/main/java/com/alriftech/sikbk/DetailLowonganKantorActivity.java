package com.alriftech.sikbk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailLowonganKantorActivity extends AppCompatActivity {
    Core core;
    SharedPreferences sp;
    TextView txtBidang, txtDeskripsi;
    String id_lowongan;
    ListView lstPelamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lowongan_kantor);

        core = new Core(this);
        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);

        lstPelamar      = findViewById(R.id.lst_pelamar);

        txtBidang       = findViewById(R.id.txt_pelam_bidang);
        txtDeskripsi    = findViewById(R.id.txt_pelam_deskripsi);

        id_lowongan = sp.getString("low_id", "");
        txtBidang.setText(sp.getString("low_bidang", ""));
        txtDeskripsi.setText(sp.getString("low_deskripsi", ""));

        new getPelamarData().execute(id_lowongan);

        lstPelamar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap item = (HashMap)parent.getItemAtPosition(position);
                Intent i = new Intent(DetailLowonganKantorActivity.this, DetailPelamarActivity.class);

                i.putExtra("id_lowongan", id_lowongan);
                i.putExtra("id_pelamar", item.get("id_pelamar").toString());
                i.putExtra("nama", item.get("nama").toString());
                i.putExtra("id_user", item.get("id_user").toString());

                startActivity(i);
            }
        });
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

    private class getPelamarData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_lowongan_pelamar/" + params[0]);
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

                    hashMap.put("id_pelamar", c.getString("id_pelamar"));
                    hashMap.put("nama", c.getString("realname"));
                    hashMap.put("pesan", c.getString("pesan"));
                    hashMap.put("pada", c.getString("dibuat_pada"));
                    hashMap.put("id_user", c.getString("id_user"));

                    mylist.add(hashMap);
                }

                SimpleAdapter sa = new SimpleAdapter(DetailLowonganKantorActivity.this, mylist, R.layout.lst_row_pelamar,
                        new String[] {"nama", "pesan", "pada", "id_user"}, new int[] {R.id.row_pelamar_nama, R.id.row_pelamar_pesan, R.id.row_pelamar_dibuat_pada, R.id.row_pelamar_id_user});
                lstPelamar.setAdapter(sa);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            core.autoSizeListview(lstPelamar, 20);
        }
    }
}
