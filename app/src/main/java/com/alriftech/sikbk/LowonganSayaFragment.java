package com.alriftech.sikbk;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LowonganSayaFragment extends Fragment {
    Core core;
    SharedPreferences sp;
    private String id_user;
    private View rv;

    private RecyclerView recyclerView;
    private TextView txtNoData;
    private LowonganSayaAdapter adapter;
    private ArrayList<LowonganSaya> lowonganSayaArrayList;

    public LowonganSayaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_lowongan_saya);

        sp = getActivity().getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = rv = inflater.inflate(R.layout.fragment_lowongan_saya, container, false);

        txtNoData = rootView.findViewById(R.id.txt_no_data);
        recyclerView = rootView.findViewById(R.id.lst_lowongan_saya_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lowonganSayaArrayList = new ArrayList<>();
        adapter = new LowonganSayaAdapter(getActivity(), lowonganSayaArrayList);
        recyclerView.setAdapter(adapter);

        new getLowonganSayaData().execute(id_user);

        return rootView;
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class getLowonganSayaData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_lowongan_saya/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    LowonganSaya ls = new LowonganSaya(
                            c.getInt("id_pelamar"),
                            c.getString("bidang"),
                            c.getString("deskripsi"),
                            c.getString("nama_kantor"),
                            c.getString("dibuat_pada"),
                            c.getString("status"),
                            c.getString("balasan")
                    );
                    lowonganSayaArrayList.add(ls);
                    adapter.notifyDataSetChanged();

                    cekCount();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}