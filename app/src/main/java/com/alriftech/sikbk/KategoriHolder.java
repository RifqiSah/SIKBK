package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class KategoriHolder extends RecyclerView.ViewHolder {
    private TextView txtNamaKategori;
    private Core core;
    private View rv;

    public KategoriHolder(View itemView) {
        super(itemView);

        rv = itemView.getRootView();
        core = new Core(rv.getContext());
        this.txtNamaKategori    = itemView.findViewById(R.id.txt_list_kategori_nama);
    }

    public void setDetails(final Kategori kategori) {
        txtNamaKategori.setText(kategori.getNama_kategori());

        Drawable dw = itemView.getBackground();
        dw.setTint(Color.parseColor(kategori.getWarna()));
        
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(rv.getContext(), R.style.Sikbk_Dialog)
                        .setTitle(R.string.t_konfirmasi)
                        .setMessage("Apakah Anda yakin ingin menghapus kategori?")
                        .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                new deleteKategori().execute(String.valueOf(kategori.getId()));
                            }
                        })
                        .setNegativeButton(R.string.btn_no, null).show();
                return false;
            }
        });
    }

    private class deleteKategori extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("kategori_hapus/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);

                if (jobj.getString("status").equals("success")) {
                    Snackbar.make(itemView.getRootView().findViewById(R.id.layout_dashboard), jobj.getString("data"), Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
