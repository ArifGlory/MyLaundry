package myproject.mylaundry.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import myproject.mylaundry.Kelas.Fasilitas;
import myproject.mylaundry.R;
import myproject.mylaundry.adapter.AdapterCariFasilitas;
import myproject.mylaundry.adapter.AdapterFasilitas;
import myproject.mylaundry.base.BaseActivity;

public class CariByFasilitasActivity extends BaseActivity {

    RecyclerView rvFasilitas;
    List<Fasilitas> fasilitasList;
    List<String> listNamaFasilitas;
    AdapterCariFasilitas adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_by_fasilitas);

        rvFasilitas = findViewById(R.id.rvFasilitas);
        fasilitasList = new ArrayList<>();
        listNamaFasilitas = new ArrayList<>();
        ref = firestore.collection("fasilitas");

        adapter = new AdapterCariFasilitas(this,fasilitasList);
        rvFasilitas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvFasilitas.setHasFixedSize(true);
        rvFasilitas.setItemAnimator(new DefaultItemAnimator());
        rvFasilitas.setAdapter(adapter);


        getDataFasilitas();
    }

    public void getDataFasilitas(){
        showLoading();
        fasilitasList.clear();

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dismissLoading();
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        Fasilitas fasilitas = doc.toObject(Fasilitas.class);
                        String nama_fasilitas = fasilitas.namaFasilitas.toLowerCase();

                        if (!listNamaFasilitas.contains(nama_fasilitas)){
                            listNamaFasilitas.add(nama_fasilitas);
                            fasilitasList.add(fasilitas);
                        }


                    }
                    adapter.notifyDataSetChanged();
                }else{
                    showErrorMessage("Terjadi kesalahan,coba lagi nanti");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissLoading();
                showErrorMessage("Terjadi kesalahan,coba lagi nanti");
                Log.d("getFasilitas",":"+e.toString());
            }
        });

    }
}
