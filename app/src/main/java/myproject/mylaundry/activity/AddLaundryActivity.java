package myproject.mylaundry.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseActivity;
import myproject.mylaundry.module.Utils;

public class AddLaundryActivity extends BaseActivity {

    EditText etNama,etHarga;
    RelativeLayout rlAlamat;
    Uri uri,file;
    private int PLACE_PICKER_REQUEST = 1;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    String alamat,latitude,longitude;
    ImageView ivLaundry;
    TextView tvAlamat;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_laundry);

        ref = firestore.collection("laundry");

        etNama      = findViewById(R.id.etNama);
        etHarga     = findViewById(R.id.etHarga);
        rlAlamat    = findViewById(R.id.rlAlamat);
        ivLaundry   = findViewById(R.id.ivLaundry);
        tvAlamat   = findViewById(R.id.tvAlamat);
        btnSimpan   = findViewById(R.id.btnSimpan);

        ivLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddLaundryActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
        rlAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(AddLaundryActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {

        // Get all edittext texts
        String getNama      = etNama.getText().toString();
        String getHarga     = etHarga.getText().toString();
        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getNama.equals("") || getNama.length() == 0 ||
                getHarga.equals("") || getHarga.length() == 0 ||
                alamat == null ||
                alamat.equals("") || alamat.equals("Pilih alamat laundry")  ||
                uri == null) {

            showErrorMessage("Semua data harus diisi");
        }
        else{
            pDialogLoading.show();
            simpanData();
        }
    }


    private void simpanData(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(AddLaundryActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(AddLaundryActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                long harga = Long.parseLong(etHarga.getText().toString());
                Laundry laundry = new Laundry(etNama.getText().toString(),
                        harga,
                        alamat,
                        latitude,
                        longitude,
                        urlGambar,
                        SharedVariable.userID
                        );
                laundry.setIdLaundry(timeStamp);

                ref.document(timeStamp).set(laundry).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        if (task.isSuccessful()){
                            showSuccessMessage("Data laundry disimpan");
                            resetKomponen();
                        }else {
                           showErrorMessage("Terjadi kesalahan, coba lagi nanti");
                            Log.d("erorUpload:","erorGambar "+task.getException().toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoading.dismiss();
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti");
                        Log.d("erorUpload:","erorGambar "+e.toString());
                    }
                });

            }
        });
    }

    private void resetKomponen(){
        etHarga.setText("");
        etNama.setText("");
        tvAlamat.setText("Pilih alamat laundry");
        uri = null;
        alamat ="";
        latitude = "";
        ivLaundry.setImageResource(R.drawable.laundry_logo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                //tvPlaceAPI.setText(toastMsg);

                tvAlamat.setText(place.getAddress());
                tvAlamat.setVisibility(View.VISIBLE);

                alamat = (String) place.getAddress();
                Double lat = place.getLatLng().latitude;
                Double lon = place.getLatLng().longitude;
                latitude = ""+lat;
                longitude = ""+lon;
                //Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivLaundry.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivLaundry.setImageURI(uri);
        }
    }
}
