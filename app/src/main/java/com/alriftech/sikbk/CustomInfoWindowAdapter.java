package com.alriftech.sikbk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.StringTokenizer;

/**
 * Created by RifqiSah on 10-Dec-17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private String tel;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.costommarkerdetails, null);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvPemilik = (TextView) view.findViewById(R.id.tv_pemilik);
        TextView tvOperasional = (TextView) view.findViewById(R.id.tv_operasional);
        TextView tvWebsite = (TextView) view.findViewById(R.id.tv_website);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tvJarak = (TextView) view.findViewById(R.id.tv_jarak);

        String data = marker.getSnippet();
        String[] items = data.split(",");

        tvTitle.setText(marker.getTitle());
        tvPemilik.setText(items[0]);
        tvOperasional.setText(items[1]);
        tvWebsite.setText(items[2]);
        tvPhone.setText(items[3]);
        tvJarak.setText(items[4]);

        return view;
    }
}
