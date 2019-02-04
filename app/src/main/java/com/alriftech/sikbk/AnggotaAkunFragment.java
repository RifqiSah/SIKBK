package com.alriftech.sikbk;

import android.content.Context;
import android.net.Uri;
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

public class AnggotaAkunFragment extends Fragment {
    Core core;

    private View rv;

    private RecyclerView recyclerView;
    private TextView txtNoData;
    private UsersAdapter adapter;
    private ArrayList<Users> usersArrayList;

    public AnggotaAkunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_daftar_anggota_user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = rv = inflater.inflate(R.layout.fragment_anggota_akun, container, false);

        txtNoData = rootView.findViewById(R.id.txt_no_data);
        recyclerView = rootView.findViewById(R.id.lst_anggota_akun_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usersArrayList = new ArrayList<>();
        adapter = new UsersAdapter(getActivity(), usersArrayList);
        recyclerView.setAdapter(adapter);

        new getUsersData().execute("3");

        return rootView;
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class getUsersData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("users/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    Users users = new Users(
                            c.getInt("id_user"),
                            c.getString("username"),
                            c.getString("email"),
                            c.getString("nomor_hp"),
                            c.getString("realname"),
                            c.getString("tanggal_lahir"),
                            c.getString("jenis_kelamin"),
                            c.getString("alamat"),
                            c.getInt("level"),
                            c.getInt("status")
                    );

                    usersArrayList.add(users);
                    adapter.notifyDataSetChanged();

                    cekCount();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
