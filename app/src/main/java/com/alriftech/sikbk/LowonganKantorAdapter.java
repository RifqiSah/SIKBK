package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LowonganKantorAdapter extends RecyclerView.Adapter<LowonganKantorHolder> {
    private Context context;
    private ArrayList<LowonganKantor> lowonganKantor;

    public LowonganKantorAdapter(Context context, ArrayList<LowonganKantor> lowonganKantor) {
        this.context = context;
        this.lowonganKantor = lowonganKantor;
    }

    @Override
    public LowonganKantorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_lowongan, parent, false);
        return new LowonganKantorHolder(view);
    }

    @Override
    public void onBindViewHolder(LowonganKantorHolder holder, int position) {
        LowonganKantor lowonganKantor2 = lowonganKantor.get(position);
        holder.setDetails(lowonganKantor2);

    }

    @Override
    public int getItemCount() {
        return lowonganKantor.size();
    }
}
