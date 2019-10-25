package myproject.mylaundry.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.UserModel;
import myproject.mylaundry.MainActivity;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseActivity;
import myproject.mylaundry.module.Utils;

public class RegisterActivity extends BaseActivity {

    EditText etNama,etEmail,etNope,etPassword,etPassword2,etAlamat;
    Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etNope = findViewById(R.id.etNope);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        etAlamat = findViewById(R.id.etAlamat);
        btnDaftar = findViewById(R.id.btnDaftar);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    public void keLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void checkValidation() {


        // Get all edittext texts
        String getFullName = etNama.getText().toString();
        String getEmailId = etEmail.getText().toString();
        String getPassword = etPassword.getText().toString();
        String getConfirmPassword = etPassword2.getText().toString();
        String getPhone = etNope.getText().toString();
        String getAlamat = etAlamat.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0
                || getAlamat.length() == 0) {

            showErrorMessage("Semua data harus diisi");

        }else if (getPhone.equals("") || getPhone.length() == 0){
            showErrorMessage("No Telepon belum diiisi");
        }
        //check valid email
        else if (!m.find()) {
            showErrorMessage("Email yang anda masukkan tidak valid");
        }
        // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword)) {
            showErrorMessage("Konfirmasi Password Salah");
        }
        // Else do signup or do your stuff
        else{
            pDialogLoading.show();
            signUp(getEmailId,getPassword);
        }

    }

    private void signUp(final String email, String passwordUser){

        fAuth.createUserWithEmailAndPassword(email,passwordUser).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d("Fauth :","createUserWithEmail:onComplete: " + task.isSuccessful());
                /**
                 * Jika sign in gagal, tampilkan pesan ke user. Jika sign in sukses
                 * maka auth state listener akan dipanggil dan logic untuk menghandle
                 * signed in user bisa dihandle di listener.
                 */

                if (!task.isSuccessful()){
                    pDialogLoading.dismiss();
                    Log.e("Eror gagal daftar ",task.getException().toString());
                    showErrorMessage("Proses pendaftarann gagal "+task.getException().toString());

                }else {

                    FirebaseUser user = fAuth.getCurrentUser();
                    String userID =  fAuth.getCurrentUser().getUid();
                    String token  = FirebaseInstanceId.getInstance().getToken();
                    //ganti nama
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etNama.getText().toString()).build();
                    user.updateProfile(profileChangeRequest);

                    UserModel userModel = new UserModel(
                            etNama.getText().toString(),
                            etEmail.getText().toString(),
                            etNope.getText().toString(),
                            "no"
                    );
                    userModel.setAlamat(etAlamat.getText().toString());
                    //untuk jika butuh verifikasi by email
                   // fAuth.getCurrentUser().sendEmailVerification();

                    DocumentReference docUser = firestore.collection("users").document(userID);
                    docUser.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            showSuccessMessage("Pendaftaran Berhasil,Silakan Login");


                            etNama.setText("");
                            etEmail.setText("");
                            etPassword.setText("");
                            etPassword2.setText("");
                            etNope.setText("");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pDialogLoading.dismiss();
                            showErrorMessage("Proses pendaftarann gagal");
                            Log.d("ErorTambahUser", e.toString());
                        }
                    });

                }
            }
        });
    }
}
