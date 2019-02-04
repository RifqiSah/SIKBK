package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    GoogleMap map;
    TextView tvLat, tvLong;

    boolean init = true;
    boolean init2 = true;
    double curLat, curLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        tvLat   = findViewById(R.id.tvd_lat);
        tvLong  = findViewById(R.id.tvd_long);

        new AlertDialog.Builder(MapActivity.this, R.style.Sikbk_Dialog)
                .setTitle(R.string.t_informasi)
                .setMessage(R.string.b_infomap)
                .setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.BROADCAST_ACTION);
        registerReceiver(ReceivefromService, filter);
    }

    public void pilihLokasi(View v) {
        Intent i = new Intent();

        i.putExtra("select_lat", tvLat.getText());
        i.putExtra("select_long", tvLong.getText());

        setResult(RESULT_OK, i);
        finish();
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            curLat = intent.getDoubleExtra("Latitude",0);
            curLong = intent.getDoubleExtra("Longitude",0);

            if (init2) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLat, curLong), 17.0f));
                init2 = false;
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Set Custom InfoWindow Adapter
//        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapActivity.this);
//        map.setInfoWindowAdapter(adapter);
        map.getUiSettings().setRotateGesturesEnabled(false);

        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION }, LocationService.MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (init) {
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true));

            init = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng loc = marker.getPosition();

        tvLat.setText(String.valueOf(loc.latitude));
        tvLong.setText(String.valueOf(loc.longitude));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng loc = marker.getPosition();

        tvLat.setText(String.valueOf(loc.latitude));
        tvLong.setText(String.valueOf(loc.longitude));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng loc = marker.getPosition();

        tvLat.setText(String.valueOf(loc.latitude));
        tvLong.setText(String.valueOf(loc.longitude));
    }
}
