package myproject.mylaundry.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import myproject.mylaundry.Kelas.Fasilitas;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.adapter.AdapterCariFasilitas;
import myproject.mylaundry.adapter.AdapterLaundry;
import myproject.mylaundry.base.BaseActivity;

public class ResultCariFasilitasActivity extends BaseActivity {

    RecyclerView rvResult;
    AdapterLaundry adapter;
    List<Laundry> laundryList;
    List<String> idLaundryList;
    Intent intent;
    String keyword;

    List<Fasilitas> fasilitasList;
    AdapterCariFasilitas adapterFasilitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_cari_fasilitas);

        rvResult = findViewById(R.id.rvResult);
        ref = firestore.collection("laundry");
        refSecond = firestore.collection("fasilitas");

        laundryList = new ArrayList<>();
        fasilitasList = new ArrayList<>();
        idLaundryList = new ArrayList<>();

        adapter = new AdapterLaundry(ResultCariFasilitasActivity.this,laundryList);

        rvResult.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvResult.setHasFixedSize(true);
        rvResult.setItemAnimator(new DefaultItemAnimator());
        rvResult.setAdapter(adapter);

        intent = getIntent();
        keyword = intent.getStringExtra("keyword");

        getDataFasilitasByKeyword();
    }


    public void getDataFasilitasByKeyword(){
        showLoading();
        refSecond.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                fasilitasList.clear();
                idLaundryList.clear();

                QuerySnapshot dc = task.getResult();
                if (dc.isEmpty()){
                    dismissLoading();
                    showInfoMessage("data belum ada");
                }

                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){

                        Fasilitas fasilitas = doc.toObject(Fasilitas.class);
                        String nama_fasilitas = fasilitas.namaFasilitas.toLowerCase();

                        if (nama_fasilitas.equals(keyword)){
                            idLaundryList.add(fasilitas.idLaundry);
                        }
                    }
                    getDataFilterLaundry();

                }else{
                    dismissLoading();
                    showErrorMessage("Pengambilan data gagal");
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }

    public void getDataFilterLaundry(){
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

                        String idLaundry   = doc.get("idLaundry").toString();

                        if (idLaundryList.contains(idLaundry)){
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

}
