package myproject.mylaundry.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.PermissionHelper;
import myproject.mylaundry.R;
import myproject.mylaundry.module.DirectionKobal;
import myproject.mylaundry.module.DirectionKobalListener;
import myproject.mylaundry.module.Route;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,PermissionHelper.PermissionListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DirectionKobalListener {

    private GoogleMap mMap;
    Intent intent;
    LatLng lokasiLaundry;
    PermissionHelper permissionHelper;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    LocationRequest request;
    GoogleApiClient client;
    Double lat,lon;
    FloatingActionButton btnRute;

    //for direction
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 6000;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<String> LatLngDriver = new ArrayList<>();
    private List<Double> list_jarak = new ArrayList<>();
    private List<LatLng> list_LatLng = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
        lokasiLaundry = intent.getParcelableExtra("lokasi");
        btnRute = findViewById(R.id.btnRute);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        permissionHelper = new PermissionHelper(this);
        permissionHelper.setPermissionListener(this);
        List<String> listPermissions = new ArrayList<>();
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionHelper.checkAndRequestPermissions(listPermissions);

        btnRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lat != null){
                    doAStar();
                }else{
                    Toast.makeText(getApplicationContext(), "lokasi bermasalah, coba lagi nanti", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(lokasiLaundry).title("Lokasi Laundry"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiLaundry,15));
    }

    private void doAStar(){

        list_jarak.clear();
        mMap.clear();

        String origin = String.valueOf(lat) + ","+ String.valueOf(lon);
        String destination = String.valueOf(lokasiLaundry.latitude) + ","+ String.valueOf(lokasiLaundry.longitude);

        if (origin.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Tolong masukkan origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Tolong masukkan destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

//Toast.makeText(getActivity(),"origin : "+origin+"& destination : "+destination,Toast.LENGTH_SHORT).show();

        try {
            new DirectionKobal(this, origin, destination).execute();
            Log.d("origin:",origin);
            Log.d("destination:",destination);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Eror Direction : "+e.toString(),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestCallBack(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionCheckDone() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(4000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Lokasi Kamu Tidak Dapat Ditemukan", Toast.LENGTH_LONG).show();
        }else{
            lat = location.getLatitude();
            lon = location.getLongitude();
            // Toast.makeText(getApplicationContext(), "lat : "+lat+" & lon:"+lon, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionKobalStart() {
        pDialogLoading.show();

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionKobalSuccess(List<Route> route) {
        pDialogLoading.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        int no = 0;
        for (Route routes : route) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routes.startLocation, 16));
            //tvDuration.setText(routes.duration.text);
            //tvDistance.setText(routes.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title("Lokasi Saya")
                    .position(routes.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title("Lokasi Laundry")
                    .position(routes.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            polylineOptions.add(routes.startLocation);
            for (int i = 0; i < routes.points.size(); i++)
                polylineOptions.add(routes.points.get(i));
            polylineOptions.add(routes.endLocation);

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            no++;
        }
    }
}
