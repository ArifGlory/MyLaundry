package myproject.mylaundry.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.MainActivity;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    ProgressBar progressBar;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        if (fbUser!=null){
            Log.d("fbUser:","ada fbuser"+fbUser.getDisplayName());
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedVariable.userID = fAuth.getCurrentUser().getUid();
            SharedVariable.nama = fAuth.getCurrentUser().getDisplayName();
            String email = fAuth.getCurrentUser().getEmail();

            DocumentReference user = firestore.collection("users").document(SharedVariable.userID);
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        String foto = doc.get("foto").toString();
                        String nope = doc.get("nope").toString();
                        String alamat = doc.get("alamat").toString();

                        SharedVariable.foto = foto;
                        SharedVariable.phone = nope;
                        SharedVariable.alamat = alamat;
                        SharedVariable.email = fAuth.getCurrentUser().getEmail();
                        i = new Intent(SplashActivity.this, BerandaPemilikActivity.class);
                        startActivity(i);

                    }else {
                        Log.d("erorGetDatauser:",task.getException().toString());
                        i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("erorGetDatauser:",e.toString());
                    i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });


        }else {
            Log.d("fbUser:","gak ada fbuser");
            i = new Intent(SplashActivity.this, BerandaUserActivity.class);
            startActivity(i);
        }

    }
}
