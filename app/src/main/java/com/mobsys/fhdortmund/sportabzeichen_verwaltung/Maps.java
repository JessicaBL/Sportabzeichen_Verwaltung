package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Maps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    DatabaseHelperSports myDbSp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myDbSp = new DatabaseHelperSports(this);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //GPS-Standortzugriff im Manifest erlaubt?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(Maps.this, "GPS-Zugriff verweigert", Toast.LENGTH_LONG).show();
        }
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        createMarkers();
    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        final double lat = latLng.latitude;
        final double lng = latLng.longitude;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disziplin anlegen?");
        builder.setCancelable(false);
        builder.setPositiveButton("Anlegen", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Maps.this, NewSports.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);


            }
        });

        builder.setNegativeButton("Nicht anlegen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void createMarkers() {
        Cursor res = myDbSp.getAllData();

        while(res.moveToNext()){
            String id=res.getString(0);
            String name=res.getString(1);
            String info=res.getString(2);
            String position_lat=res.getString(3);
            String position_lng=res.getString(4);

            LatLng latlng = new LatLng(Double.parseDouble(position_lat), Double.parseDouble(position_lng));
            mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(id+", "+name+", Einheit: "+info));
        }

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disziplin-Optionen")
                .setItems(R.array.sports, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                        String title = marker.getTitle();
                        String[] splitResult = title.split(",");
                        String result = splitResult[0];

                        if (which ==0){
                            //eintragen
                            Intent intent = new Intent(Maps.this, NewResult.class);
                            intent.putExtra("id", result);
                            startActivity(intent);
                        }

                        if(which==1){
                            //alle anzeigen
                            Intent intent = new Intent(Maps.this, ShowResults.class);
                            intent.putExtra("id", result);
                            startActivity(intent);
                        }

                        else if (which ==2){
                            //löschen

                            myDbSp.deleteData(result);

                            marker.remove();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }


}

