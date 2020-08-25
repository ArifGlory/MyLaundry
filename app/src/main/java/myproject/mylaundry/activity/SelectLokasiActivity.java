package myproject.mylaundry.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.dmoral.toasty.Toasty;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;

public class SelectLokasiActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnSelectLokasi;
    LatLng centerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lokasi);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSelectLokasi = findViewById(R.id.btnSelectLokasi);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng lokasiTekno = new LatLng(-5.382109, 105.257912);
        // Add a marker  in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiTekno,15));


        btnSelectLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedVariable.selectedLokasi = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
                Toasty.success(getApplicationContext(),"Lokasi Dipilih", Toast.LENGTH_SHORT,true).show();
                onBackPressed();
            }
        });
    }
}
