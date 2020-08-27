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
import android.widget.TextView;
import android.widget.Toast;

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

import myproject.mylaundry.Kelas.Fasilitas;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseActivity;
import myproject.mylaundry.module.Utils;

public class AddFasilitasActivity extends BaseActivity {

    Intent intent;
    Laundry laundry;
    EditText etNama,etHarga;
    ImageView ivLaundry;
    Button btnSimpan;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uri,file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fasilitas);

        intent = getIntent();
        laundry = (Laundry) intent.getSerializableExtra("laundry");
        ref = firestore.collection("fasilitas");

        etNama      = findViewById(R.id.etNama);
        etHarga     = findViewById(R.id.etHarga);
        ivLaundry   = findViewById(R.id.ivLaundry);
        btnSimpan   = findViewById(R.id.btnSimpan);

        ivLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddFasilitasActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
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
                Toast.makeText(AddFasilitasActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(AddFasilitasActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                long harga = Long.parseLong(etHarga.getText().toString());

                Fasilitas fasilitas = new Fasilitas(
                        timeStamp,
                        etNama.getText().toString(),
                        urlGambar,
                        laundry.getIdLaundry(),
                        harga,
                        laundry.getIdPemilik()
                );

                ref.document(timeStamp).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        if (task.isSuccessful()){
                            showSuccessMessage("Data fasilitas disimpan");
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

    private void checkValidation() {

        // Get all edittext texts
        String getNama      = etNama.getText().toString();
        String getHarga     = etHarga.getText().toString();
        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getNama.equals("") || getNama.length() == 0 ||
                getHarga.equals("") || getHarga.length() == 0 ||
                uri == null) {

            showErrorMessage("Semua data harus diisi");
        }
        else{
            pDialogLoading.show();
            simpanData();
        }
    }

    private void resetKomponen(){
        etHarga.setText("");
        etNama.setText("");
        uri = null;
        ivLaundry.setImageResource(R.drawable.laundry_logo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView


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
