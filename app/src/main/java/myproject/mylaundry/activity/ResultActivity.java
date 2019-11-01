package myproject.mylaundry.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.adapter.AdapterLaundry;
import myproject.mylaundry.base.BaseActivity;

public class ResultActivity extends BaseActivity {

    RecyclerView rvResult;
    TextView tvJudul;
    Intent intent;
    String tipe;
    AdapterLaundry adapter;
    List<Laundry> laundryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ref = firestore.collection("laundry");
        tvJudul = findViewById(R.id.tvJudul);
        rvResult = findViewById(R.id.rvResult);

        laundryList = new ArrayList<>();
        adapter = new AdapterLaundry(ResultActivity.this,laundryList);

        rvResult.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvResult.setHasFixedSize(true);
        rvResult.setItemAnimator(new DefaultItemAnimator());
        rvResult.setAdapter(adapter);

        intent = getIntent();
        tipe = intent.getStringExtra("tipe");

        if (tipe.equals("harga")){
            tvJudul.setText("Pencarian Harga");
            getDataByHarga();
        }else if (tipe.equals("alamat")){
            tvJudul.setText("Pencarian Alamat");
            getDataByAlamat();
        }
    }

    public void getDataByAlamat(){
        showLoading();
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                laundryList.clear();
                adapter.notifyDataSetChanged();

                QuerySnapshot dc = task.getResult();
                if (dc.isEmpty()){
                    dismissLoading();
                    showInfoMessage("data belum ada");
                }

                if (task.isSuccessful()){
                    dismissLoading();
                    for (DocumentSnapshot doc : task.getResult()){

                        String alamat   = doc.get("alamat").toString();
                        alamat  = alamat.toLowerCase();
                        Log.d("resultActivity","alamat di db :"+alamat);
                        String keyword = SharedVariable.keyword;
                        keyword = keyword.toLowerCase();

                        if (alamat.contains(keyword)){
                            Log.d("resultActivity:","cari alamat data ditemukan!, alamat :"+alamat);
                            String idLaundry = doc.getString("idLaundry");
                            Laundry laundry = doc.toObject(Laundry.class);
                            laundry.setIdLaundry(idLaundry);
                            laundryList.add(laundry);
                        }

                    }
                    adapter.notifyDataSetChanged();

                }else{
                    dismissLoading();
                    showErrorMessage("Pengambilan data gagal");
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }

    public void getDataByHarga(){
        showLoading();
        ref.whereLessThan("hargaPerKg",SharedVariable.maxHarga).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                laundryList.clear();
                adapter.notifyDataSetChanged();

                QuerySnapshot dc = task.getResult();
                if (dc.isEmpty()){
                    dismissLoading();
                   showInfoMessage("data belum ada");
                }

                if (task.isSuccessful()){
                   dismissLoading();
                    for (DocumentSnapshot doc : task.getResult()){
                        String idLaundry = doc.getString("idLaundry");
                       Laundry laundry = doc.toObject(Laundry.class);
                       laundry.setIdLaundry(idLaundry);
                        laundryList.add(laundry);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    dismissLoading();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissLoading();
                new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }
}
