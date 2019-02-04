package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class KantorAdapter extends RecyclerView.Adapter<KantorHolder>{
    private Context context;
    private ArrayList<Kantor> kantor;

    public KantorAdapter(Context context, ArrayList<Kantor> kantor) {
        this.context = context;
        this.kantor = kantor;
    }

    @Override
    public KantorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_kantor, parent, false);
        return new KantorHolder(view);
    }

    @Override
    public void onBindViewHolder(KantorHolder holder, int position) {
        Kantor kantor2 = kantor.get(position);
        holder.setDetails(kantor2);

    }

    @Override
    public int getItemCount() {
        return kantor.size();
    }
}
