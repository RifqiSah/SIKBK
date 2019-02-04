package com.alriftech.sikbk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

public class LowonganCursorAdapter extends SimpleCursorAdapter {
    SharedPreferences sp;

    public LowonganCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final String id           = cursor.getString(cursor.getColumnIndexOrThrow("id_lowongan"));
        final String bidang       = cursor.getString(cursor.getColumnIndexOrThrow("bidang"));
        final String deskripsi    = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));

        TextView txtBidang = view.findViewById(R.id.sc_lowongan_bidang);
        TextView txtDeskripsi = view.findViewById(R.id.sc_lowongan_deskripsi);
        View divider = view.findViewById(R.id.sc_divider);

        txtBidang.setText(bidang);
        txtDeskripsi.setText(deskripsi);

        view.setBackgroundColor(Color.WHITE);

        if (cursor.isLast())
            divider.setVisibility(View.INVISIBLE);
        else
            divider.setVisibility(View.VISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getRootView().getContext();

                sp = c.getSharedPreferences("SIKBK", Context.MODE_PRIVATE);
                sp.edit().putInt("id_lowongan", Integer.parseInt(id)).apply();

                c.startActivity(new Intent(c, DetailLowonganActivity.class));
            }
        });
    }
}
