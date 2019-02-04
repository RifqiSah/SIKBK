package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LowonganAdapter extends RecyclerView.Adapter<LowonganHolder>{

    private Context context;
    private ArrayList<Lowongan> lowongan;

    public LowonganAdapter(Context context, ArrayList<Lowongan> lowongan) {
        this.context = context;
        this.lowongan = lowongan;
    }

    @Override
    public LowonganHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_lowongan_all, parent, false);
        return new LowonganHolder(view);
    }

    @Override
    public void onBindViewHolder(LowonganHolder holder, int position) {
        Lowongan lowongan2 = lowongan.get(position);
        holder.setDetails(lowongan2);

    }

    @Override
    public int getItemCount() {
        return lowongan.size();
    }
}
