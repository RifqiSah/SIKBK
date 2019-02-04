package com.alriftech.sikbk;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class KantorFragment extends Fragment {
    Core core;
    SharedPreferences sp;
    private String id_user;
    private View rv;

    private RecyclerView recyclerView;
    private TextView txtNoData;
    private KantorAdapter adapter;
    private ArrayList<Kantor> kantorArrayList;

    public KantorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_kantor);

        sp = getActivity().getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = rv = inflater.inflate(R.layout.fragment_kantor, container, false);

        txtNoData = rootView.findViewById(R.id.txt_no_data);
        recyclerView = rootView.findViewById(R.id.lst_kantor_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        kantorArrayList = new ArrayList<>();
        adapter = new KantorAdapter(getActivity(), kantorArrayList);
        recyclerView.setAdapter(adapter);

        new getKantorData().execute(id_user);

        return rootView;
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class getKantorData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_kantor/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    Kantor kantor = new Kantor(
                            c.getInt("id_user"),
                            c.getInt("id_kantor"),
                            c.getDouble("latitude"),
                            c.getDouble("longitude"),
                            c.getString("nama_kantor"),
                            c.getString("jam_buka"),
                            c.getString("jam_tutup"),
                            c.getString("alamat")
                    );
                    kantorArrayList.add(kantor);
                    adapter.notifyDataSetChanged();

                    cekCount();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}