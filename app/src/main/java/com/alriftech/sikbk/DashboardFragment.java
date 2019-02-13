package com.alriftech.sikbk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment {
    Core core;
    View rv;

    private RecyclerView recyclerView;
    private TextView txtNoData;
    private LowonganAdapter adapter;
    private ArrayList<Lowongan> lowonganArrayList;
    private ArrayList<String> tabKategori;

    private LinearLayoutManager mLlm;
    private int pos;
    private int curPage = 1;
    private double curLat, curLong;

    // Search
    private SearchView searchView;
    private ArrayList<HashMap<String, String>> lowonganData = new ArrayList<>();
    private LowonganCursorAdapter mAdapter;
    // End Search

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.BROADCAST_ACTION);
        getActivity().registerReceiver(ReceivefromService, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getActivity().unregisterReceiver(ReceivefromService);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        core = new Core(getActivity());
        getActivity().setTitle(R.string.act_dashboard);

        setHasOptionsMenu(true);

        mAdapter = new LowonganCursorAdapter(getContext(),
                R.layout.item_row_search_lowongan,
                null,
                new String[] {"id_lowongan", "bidang", "deskripsi", "alamat"},
                new int[] {R.id.sc_lowongan_id, R.id.sc_lowongan_bidang, R.id.sc_lowongan_deskripsi, R.id.sc_lowongan_alamat},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.mnusearch, menu);
        MenuItem searchItem = menu.findItem(R.id.mnu_search);

        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Cari Lowongan");
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SikbkLog", newText);
                populateAdapter(newText);

                return false;
            }
        });
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[] {BaseColumns._ID, "id_lowongan", "bidang", "deskripsi", "alamat"});
        for (int i = 0; i < lowonganData.size(); i++) {
            HashMap<String, String> hashmap = lowonganData.get(i);

            if (hashmap.get("bidang").toLowerCase().matches("(.*)" + query.toLowerCase() + "(.*)") ||
                    hashmap.get("deskripsi").toLowerCase().matches("(.*)" + query.toLowerCase() + "(.*)") ||
                    hashmap.get("alamat").toLowerCase().matches("(.*)" + query.toLowerCase() + "(.*)"))
                c.addRow(new Object[] {
                        i,
                        hashmap.get("id_lowongan"),
                        hashmap.get("bidang"),
                        hashmap.get("deskripsi"),
                        hashmap.get("alamat")
                });
        }
        mAdapter.changeCursor(c);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = rv = inflater.inflate(R.layout.fragment_dashboard, container, false);

        new getKategoriData().execute();

        txtNoData = rootView.findViewById(R.id.txt_no_lowongan);
        recyclerView = rootView.findViewById(R.id.lst_lowongan_fragment);

        mLlm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLlm);

        lowonganArrayList = new ArrayList<>();
        adapter = new LowonganAdapter(getActivity(), lowonganArrayList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(scrollData());

        new getNextLowonganData().execute("all", String.valueOf(curPage));
        return rootView;
    }

    private EndlessOnScrollListener scrollData() {
        return new EndlessOnScrollListener() {
            @Override
            public void onLoadMore() {
                curPage++;

                if (pos == 0)
                    new getNextLowonganData().execute("all", String.valueOf(curPage));
                else
                    new getNextLowonganData().execute(String.valueOf(pos), String.valueOf(curPage));
            }
        };
    }

    private void initTab(View v) {
        TabLayout tab = v.findViewById(R.id.tab_lowongan_kategori);

        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);

        tab.addTab(tab.newTab().setText("Semua"));
        for (int i = 0; i <= tabKategori.size() - 1; i++)
            tab.addTab(tab.newTab().setText(tabKategori.get(i)));

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clearLowonganData();
                recyclerView.addOnScrollListener(scrollData());

                pos = tab.getPosition();
                if (pos == 0)
                    new getNextLowonganData().execute("all", String.valueOf(curPage));
                else
                    new getNextLowonganData().execute(String.valueOf(pos), String.valueOf(curPage));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void cekCount() {
        if (adapter.getItemCount() > 0) {
            txtNoData.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void clearLowonganData() {
        curPage = 1;

        lowonganData.clear();
        lowonganArrayList.clear();
        adapter.notifyDataSetChanged();
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            curLat = intent.getDoubleExtra("Latitude",0);
            curLong = intent.getDoubleExtra("Longitude",0);
        }
    };

    private class getKategoriData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("kategori");
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    tabKategori.add(c.getString("nama_kategori"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            initTab(rv);
        }

        @Override
        protected void onPreExecute() {
            tabKategori = new ArrayList<>();
        }
    }

    private class getNextLowonganData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = "";
            if (params[0].equals("all"))
                url = core.API("lowongan_all_new", params[1]);
            else
                url = core.API("lowongan_all_new/" + params[0], params[1]);

            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    DecimalFormat df2 = new DecimalFormat("#.##");
                    String jarak;

                    if (curLat == 0 || curLong == 0)
                        jarak = df2.format(0) + " KM";
                    else
                        jarak = df2.format(core.hitungJarak(curLat, curLong, c.getDouble("latitude"), c.getDouble("longitude"))) + " KM";

                    Lowongan lowongan = new Lowongan(
                            c.getInt("id_lowongan"),
                            c.getString("bidang"),
                            c.getString("nama_kantor"),
                            c.getString("deskripsi"),
                            c.getInt("jumlah"),
                            core.formatDate(c.getString("waktu")),
                            c.getString("id_kategori")+ ";" + c.getString("nama_kategori") + ";" + c.getString("warna"),
                            jarak);

                    lowonganArrayList.add(lowongan);
                    adapter.notifyDataSetChanged();

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("id_lowongan", c.getString("id_lowongan"));
                    hashMap.put("bidang", c.getString("bidang"));
                    hashMap.put("deskripsi", c.getString("deskripsi"));
                    hashMap.put("alamat", c.getString("alamat"));
                    lowonganData.add(hashMap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cekCount();
        }
    }
}
