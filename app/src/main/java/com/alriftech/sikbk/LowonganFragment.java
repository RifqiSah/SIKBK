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

public class LowonganFragment extends Fragment {
    Core core;
    SharedPreferences sp;
    private String id_user;

    private TextView txtNoData;
    private RecyclerView recyclerView;
    private LowonganKantorAdapter adapter;
    private ArrayList<LowonganKantor> lowonganKantorArrayList;

    public LowonganFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_lowongan);

        sp = getActivity().getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lowongan, container, false);

        txtNoData = rootView.findViewById(R.id.txt_no_data);
        recyclerView = rootView.findViewById(R.id.lst_lowongan_kantor_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lowonganKantorArrayList = new ArrayList<>();
        adapter = new LowonganKantorAdapter(getActivity(), lowonganKantorArrayList);
        recyclerView.setAdapter(adapter);

        new getLowonganKantorData().execute(id_user);
        return rootView;
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class getLowonganKantorData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_lowongan/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    LowonganKantor lowonganKantor = new LowonganKantor(
                            c.getInt("id_lowongan"),
                            c.getString("bidang"),
                            c.getString("id_kategori")+ ";" + c.getString("nama_kategori") + ";" + c.getString("warna"),
                            c.getInt("jumlah"),
                            c.getString("deskripsi"),
                            c.getString("nama_kantor"));
                    lowonganKantorArrayList.add(lowonganKantor);
                    adapter.notifyDataSetChanged();

                    cekCount();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}