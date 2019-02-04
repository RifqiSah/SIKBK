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

public class KategoriFragment extends Fragment {
    Core core;
    SharedPreferences sp;
    private String id_user;
    private View rv;

    private RecyclerView recyclerView;
    private TextView txtNoData;
    private KategoriAdapter adapter;
    private ArrayList<Kategori> kategoriArrayList;

    public KategoriFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_kategori);

        sp = getActivity().getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = rv = inflater.inflate(R.layout.fragment_kategori, container, false);

        txtNoData = rootView.findViewById(R.id.txt_no_data);
        recyclerView = rootView.findViewById(R.id.lst_kategori_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        kategoriArrayList = new ArrayList<>();
        adapter = new KategoriAdapter(getActivity(), kategoriArrayList);
        recyclerView.setAdapter(adapter);

        new getKategoriData().execute();

        return rootView;
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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

                    Kategori kategori = new Kategori(
                            c.getInt("id_kategori"),
                            c.getString("nama_kategori"),
                            c.getString("warna")
                    );

                    kategoriArrayList.add(kategori);
                    adapter.notifyDataSetChanged();

                    cekCount();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
