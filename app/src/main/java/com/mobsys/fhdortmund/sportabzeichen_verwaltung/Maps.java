package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class Maps extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;
    List<Marker> markers = new ArrayList<Marker>();
    final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
    final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDbSp = new DatabaseHelperSports(this);
        myDbSt = new DatabaseHelperStation(this);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_location){
            Location myLocation  = mMap.getMyLocation();
            if(myLocation!=null){
                double dLatitude = myLocation.getLatitude();
                double dLongitude = myLocation.getLongitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 14));
            }
            else
            {
                Toast.makeText(this, "Standort kann nicht abgerufen werden", Toast.LENGTH_LONG).show();
            }
        }

        if(id==R.id.action_show_endurance){
            for(Marker marker : markers)
            {
                String title = marker.getTitle().toString();
                String[]splitResult = title.split(" ");
                String category = splitResult[2];
                if(!category.equals("Ausdauer:")){
                    marker.setVisible(false);
                }
                else marker.setVisible(true);
            }
        }

        if(id==R.id.action_show_strength){
            for(Marker marker : markers)
            {
                String title = marker.getTitle().toString();
                String[]splitResult = title.split(" ");
                String category = splitResult[2];
                if(!category.equals("Kraft:")){
                    marker.setVisible(false);
                }
                else marker.setVisible(true);
            }
        }
        if(id==R.id.action_show_agility){
            for(Marker marker : markers)
            {
                String title = marker.getTitle().toString();
                String[]splitResult = title.split(" ");
                String category = splitResult[2];
                if(!category.equals("Schnelligkeit:")){
                    marker.setVisible(false);
                }
                else marker.setVisible(true);
            }
        }
        if(id==R.id.action_show_coordination){
            for(Marker marker : markers)
            {
                String title = marker.getTitle().toString();
                String[]splitResult = title.split(" ");
                String category = splitResult[2];
                if(!category.equals("Koordination:")){
                    marker.setVisible(false);
                }
                else marker.setVisible(true);
            }
        }
        if(id==R.id.action_show_all){
            for(Marker marker : markers)
            {
                    marker.setVisible(true);

            }
        }
        if (id==android.R.id.home){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
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
        checkGPS();
        LocationManager locationManager =
                (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation_byGps =
                locationManager.getLastKnownLocation(gpsLocationProvider);
        Location lastKnownLocation_byNetwork =
                locationManager.getLastKnownLocation(networkLocationProvider);
        if(lastKnownLocation_byGps!=null){
            double dLatitude = lastKnownLocation_byGps.getLatitude();
            double dLongitude = lastKnownLocation_byGps.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 14));
        }
        else if(lastKnownLocation_byNetwork!=null){
            double dLatitude = lastKnownLocation_byNetwork.getLatitude();
            double dLongitude = lastKnownLocation_byNetwork.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 14));
        }
    }


    public void checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS ist deaktiviert, Standort kann nicht abgerufen werden.")
                    .setCancelable(false)
                    .setPositiveButton("GPS-Einstellungen",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("abbrechen",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
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
                intent.putExtra("lat", String.valueOf(lat));
                intent.putExtra("lng", String.valueOf(lng));
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
        Cursor res = myDbSt.getAllData();

        while(res.moveToNext()){
            String id=res.getString(0);
            String name=res.getString(1);
            String position_lat=res.getString(2);
            String position_lng=res.getString(3);


              LatLng latlng = new LatLng(Double.parseDouble(position_lat), Double.parseDouble(position_lng));

            Marker marker = mMap.addMarker(new MarkerOptions()
                           .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latlng)
                           .title(id+" - " + name));
                            markers.add(marker);

        }

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {


        LayoutInflater li = LayoutInflater.from(Maps.this);

        final View promptsView = li.inflate(R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Maps.this);

        alertDialogBuilder.setView(promptsView);


        final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner_dialog);
        mSpinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        String title = marker.getTitle();
        String[] splitResult = title.split(" ");
        final String result = splitResult[0];
        ArrayList<String> SpinnerList = CreateSportsList(result);

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(Maps.this,
                android.R.layout.simple_spinner_dropdown_item, SpinnerList);

        mSpinner.setAdapter(adp);

        alertDialogBuilder.setTitle("Disziplin auswählen")
                .setItems(R.array.sports, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            //eintragen
                            String item = (String) mSpinner.getSelectedItem();
                            String[] splitResult_sp = item.split(": ");
                            Intent intent = new Intent(Maps.this, NewResult.class);
                            intent.putExtra("sports", splitResult_sp[1]);
                            startActivity(intent);
                        }
                        if (which == 1) {
                            //alle anzeigen
                            String item = (String) mSpinner.getSelectedItem();
                            String[] splitResult_sp = item.split(": ");
                            Intent intent = new Intent(Maps.this, ShowResults.class);
                            intent.putExtra("sports", splitResult_sp[1]);
                            startActivity(intent);
                        }
                        if (which == 2) {
                            Intent intent = new Intent(Maps.this, ChangeStation.class);
                            intent.putExtra("id_station", result);
                            startActivity(intent);
                        }

                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    public  ArrayList<String> CreateSportsList(String id_station) {
        Cursor res = myDbSt.selectSingleData(id_station);
        res.moveToFirst();
        String all_sports=res.getString(4);

        String split_all_sports[] = all_sports.split(",");

        ArrayList<String> SpinnerList = new ArrayList<String>();

        for (String s:split_all_sports){
            Cursor res_sports = myDbSp.selectSingleData(s);
            res_sports.moveToFirst();

            String category_name= res_sports.getString(1);
            String sports_name= res_sports.getString(2);


            SpinnerList.add(category_name+": "+sports_name);
        }
        return SpinnerList;
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

