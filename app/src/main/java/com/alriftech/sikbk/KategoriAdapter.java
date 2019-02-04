package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriHolder> {
    private Context context;
    private ArrayList<Kategori> kategori;

    public KategoriAdapter(Context context, ArrayList<Kategori> kategori) {
        this.context = context;
        this.kategori = kategori;
    }

    @Override
    public KategoriHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_kategori, parent, false);
        return new KategoriHolder(view);
    }

    @Override
    public void onBindViewHolder(KategoriHolder holder, int position) {
        Kategori kategori2 = kategori.get(position);
        holder.setDetails(kategori2);

    }

    @Override
    public int getItemCount() {
        return kategori.size();
    }
}
