package myproject.mylaundry.base;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


/**
 * Created by Miroslaw Stanek on 19.01.15.
 */
public class BaseActivity extends AppCompatActivity {

    public SweetAlertDialog pDialogLoading,pDialodInfo;
    public FirebaseFirestore firestore;
    public FirebaseAuth fAuth;
    public FirebaseUser fbUser;
    public CollectionReference ref;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    public void showLoading(){
        pDialogLoading.show();
    }

    public void dismissLoading(){
        pDialogLoading.dismiss();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    public void showErrorMessage(String message){
        Toasty.error(getApplicationContext(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showSuccessMessage(String message){
        Toasty.success(getApplicationContext(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showWarningMessage(String message){
        Toasty.warning(getApplicationContext(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showInfoMessage(String message){
        Toasty.info(getApplicationContext(),message, Toast.LENGTH_SHORT,true).show();
    }


}
