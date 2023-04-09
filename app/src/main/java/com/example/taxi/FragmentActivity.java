package com.example.taxi;

import static com.example.taxi.EditProfile.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.common.api.internal.zaag;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LastLocationRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import kotlin.jvm.Synchronized;

public class  FragmentActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {

    boolean isPermissionGranted;
    GoogleMap gMap;
    FloatingActionButton fb;
    private FusedLocationProviderClient fLocationP_Client;
    private Object LocationRequest;
    List<MarkerOptions> listPoints;
    Location mLocation;
    Marker locationMarker;
    GoogleApiClient mGoogleClient;
    LocationRequest mLocationRequest;
    Button btnGo, btnPhelindaba, btnTau, btnUnivesters;
    MarkerOptions current, destination, phelindaba, tau, univesters;
    Polyline currentPolyline;
    double distance;
    float[] distanceArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        listPoints = new ArrayList<>();

        btnUnivesters = findViewById(R.id.btnUnivesters);
        btnUnivesters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                univesters = new MarkerOptions().position(new LatLng(-29.1186,26.2263));
                locationMarker = gMap.addMarker(univesters);
                univesters.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                univesters.title("Univesters Taxi spot");
                String url = getUrl(current.getPosition(),univesters.getPosition(),"walking");
                new FetchURL(FragmentActivity.this).execute(url,"walking");
            }
        });

        btnTau = findViewById(R.id.btnTau);
        btnTau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                tau = new MarkerOptions().position(new LatLng(-29.1170,26.2259));
                tau.title("Tau Taxi Spot");
                tau.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                locationMarker = gMap.addMarker(tau);
                String url = getUrl(current.getPosition(),tau.getPosition(),"walking");
                new FetchURL(FragmentActivity.this).execute(url,"walking");
            }
        });


        btnPhelindaba = findViewById(R.id.btnPhelinda);
        btnPhelindaba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                phelindaba = new MarkerOptions().position(new LatLng(-29.087217,26.154898));
                phelindaba.title("Phelindaba Taxi Spot");
                phelindaba.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                locationMarker = gMap.addMarker(phelindaba);
                String url = getUrl(current.getPosition(),phelindaba.getPosition(),"walking");
                new FetchURL(FragmentActivity.this).execute(url,"walking");
            }
        });

        //Rocklands marker uses name btnGo
        btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                current = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                destination = new MarkerOptions().position(new LatLng(-29.1181,26.2248));
                destination.title("Rocklands Taxi spot");
                destination.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                locationMarker = gMap.addMarker(destination);
                String url = getUrl(current.getPosition(),destination.getPosition(),"walking");
                new FetchURL(FragmentActivity.this).execute(url,"walking");

            }
        });

        fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FragmentActivity.this, Update.class);
                startActivity(intent);
            }
        });

        checkMyPermission();

        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            supportMapFragment.getMapAsync(this);
            mapInitializer();
        }


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.key);
        return url;
    }

    private void mapInitializer() {

        LocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

        fLocationP_Client = LocationServices.getFusedLocationProviderClient(this);
    }

    private void checkMyPermission () {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                Toast.makeText(FragmentActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;

        MarkerOptions rocklands = new MarkerOptions().position(new LatLng(-29.087212,26.154898));
        MarkerOptions phelindaba = new MarkerOptions().position(new LatLng(-29.11,26.22));

        listPoints.add(rocklands);
        listPoints.add(phelindaba);

        fLocationP_Client.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FragmentActivity.this, "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,3.0f));

            }
        });
        buildGoogleApiClient();
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
       //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(setMyLocationEnabled(true),15));

        btnUnivesters = findViewById(R.id.btnUnivesters);
        btnUnivesters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current = new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                univesters = new MarkerOptions().position(new LatLng(-29.1186,26.2263));
                locationMarker = gMap.addMarker(univesters);
                univesters.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                univesters.title("Univesteres Taxi spot");
                String url = getUrl(current.getPosition(),univesters.getPosition(),"walking");
                new FetchURL(FragmentActivity.this).execute(url,"walking");
                //distance = current;
                        //currentPolyline.setTag();
            }
        });

    }
    protected synchronized void buildGoogleApiClient(){

        mGoogleClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleClient.connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        mLocation =location;
        if(locationMarker != null){
            locationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        locationMarker = gMap.addMarker(markerOptions);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient,mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = gMap.addPolyline((PolylineOptions) values[0]);
    }
}


