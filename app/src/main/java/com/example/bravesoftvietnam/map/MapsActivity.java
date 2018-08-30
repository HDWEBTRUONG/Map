package com.example.bravesoftvietnam.map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
    //https://maps.googleapis.com/maps/api/directions/json?origin=10.792533,106.653009&destination=10.7784471,106.6651842&sensor=false&mode=driving
    private GoogleMap mMap;
    private LocationManager locationManager;
    private double longitude, latitude;

    String address;
    String city;
    String state;
    String country;
    String postalCode;
    String knownName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startGpsUpdate(this);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    public void startGpsUpdate(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (null == locationManager) {
            return;
        }

        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();

        // Accuracyを指定(低精度)
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // PowerRequirementを指定(低消費電力)
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // ロケーションプロバイダの取得
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //this condition never go thought this body, because we already require permission at the begining when the app start
            //this condition written there to make sure that the warning of the lines of code bellow won't appear!
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                getAddress(longitude, latitude);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {


            }
        });

    }

    private void getAddress(double longitude, double latitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();//Phường
                state = addresses.get(0).getAdminArea();//TP
                country = addresses.get(0).getCountryName();//QG
                postalCode = addresses.get(0).getPostalCode();//Null
                knownName = addresses.get(0).getFeatureName();//Number address
                Log.d("ttt", address + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode + "\n" + knownName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(latitude, longitude);
        Log.d("hhhh",latitude +"\n" +longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                //lstLatLngs.add(point);
                // mMap.clear();
                getAddress(point.longitude, point.latitude);
                mMap.addMarker(new MarkerOptions().position(point).title(address));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 15));
                double distance;
                Location locationA = new Location("");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("");
                locationB.setLatitude(point.latitude);
                locationB.setLongitude(point.longitude);
                distance = locationA.distanceTo(locationB) / 1000;
                //String.format("%.1f", distance
            }
        });
    }

    public void SearchLocation(View view) {
        EditText edt = findViewById(R.id.ed_search);
        if (!edt.getText().toString().isEmpty()) {
            GetLoctionFromAddress fromAddress = new GetLoctionFromAddress(mMap, edt.getText().toString());
            fromAddress.execute();
        }
    }
}
