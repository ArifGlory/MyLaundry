package myproject.mylaundry.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.text.NumberFormat;
import java.util.Locale;

import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseActivity;

public class DetailLaundryActivity extends BaseActivity {

    Intent intent;
    Laundry laundry;
    TextView tvNamaLaundry,tvHarga,tvAlamat;
    ImageView ivLaundry;
    Button btnLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laundry);

        tvNamaLaundry = findViewById(R.id.tvNamaLaundry);
        tvHarga = findViewById(R.id.tvHarga);
        tvAlamat = findViewById(R.id.tvAlamat);
        ivLaundry = findViewById(R.id.ivLaundry);
        btnLokasi = findViewById(R.id.btnLokasi);

        intent = getIntent();
        laundry = (Laundry) intent.getSerializableExtra("laundry");

        tvNamaLaundry.setText(laundry.getNamaLaundry());
        tvAlamat.setText(laundry.getAlamat());
        Glide.with(this)
                .load(laundry.getFoto())
                .into(ivLaundry);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        Double myHarga = Double.valueOf(laundry.getHargaPerKg());
        tvHarga.setText(""+formatRupiah.format((double) myHarga));

        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lati = Double.parseDouble(laundry.getLat());
                double longi = Double.parseDouble(laundry.getLon());

                LatLng lokasi = new LatLng(lati,longi);
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("lokasi",lokasi);
                startActivity(intent);
            }
        });

    }
}
