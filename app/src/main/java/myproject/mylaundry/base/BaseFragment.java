package myproject.mylaundry.base;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.adapter.AdapterLaundry;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {


    public BaseFragment() {
        // Required empty public constructor
    }

    CarouselView carouselView;
    public SweetAlertDialog pDialogLoading,pDialodInfo;
    public FirebaseFirestore firestore;
    public FirebaseAuth fAuth;
    public FirebaseUser fbUser;
    public CollectionReference ref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        FirebaseApp.initializeApp(getActivity());
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        pDialogLoading = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
    }

    public void showDialogLoading(){
        pDialogLoading.show();
    }

    public void dismissLoading(){
        pDialogLoading.dismiss();
    }

    public void showErrorMessage(String message){
        Toasty.error(getActivity(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showSuccessMessage(String message){
        Toasty.success(getActivity(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showWarningMessage(String message){
        Toasty.warning(getActivity(),message, Toast.LENGTH_SHORT,true).show();
    }

    public void showInfoMessage(String message){
        Toasty.info(getActivity(),message, Toast.LENGTH_SHORT,true).show();
    }






}
