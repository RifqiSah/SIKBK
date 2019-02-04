package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LowonganSayaAdapter extends RecyclerView.Adapter<LowonganSayaHolder> {
    private Context context;
    private ArrayList<LowonganSaya> lowonganSaya;

    public LowonganSayaAdapter(Context context, ArrayList<LowonganSaya> lowonganSaya) {
        this.context = context;
        this.lowonganSaya = lowonganSaya;
    }

    @Override
    public LowonganSayaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_lowongan_saya, parent, false);
        return new LowonganSayaHolder(view);
    }

    @Override
    public void onBindViewHolder(LowonganSayaHolder holder, int position) {
        LowonganSaya lowonganSaya2 = lowonganSaya.get(position);
        holder.setDetails(lowonganSaya2);

    }

    @Override
    public int getItemCount() {
        return lowonganSaya.size();
    }
}
