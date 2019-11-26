package myproject.mylaundry.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import myproject.mylaundry.Kelas.Fasilitas;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.adapter.AdapterFasilitas;
import myproject.mylaundry.base.BaseActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FasilitasActivity extends BaseActivity {

    RecyclerView rvFasilitas;
    FloatingActionButton btnCreate;
    List<Fasilitas> fasilitasList;
    AdapterFasilitas adapter;
    Laundry laundry;
    Intent intent;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasilitas);

        intent = getIntent();
        laundry = (Laundry) intent.getSerializableExtra("laundry");

        rvFasilitas = findViewById(R.id.rvFasilitas);
        btnCreate = findViewById(R.id.btnCreate);
        fasilitasList = new ArrayList<>();
        ref = firestore.collection("fasilitas");

        adapter = new AdapterFasilitas(this,fasilitasList);
        rvFasilitas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvFasilitas.setHasFixedSize(true);
        rvFasilitas.setItemAnimator(new DefaultItemAnimator());
        rvFasilitas.setAdapter(adapter);

        if (!SharedVariable.userID.equals("")){
            btnCreate.setVisibility(View.VISIBLE);
        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFasilitasActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("laundry",laundry);
                startActivity(intent);
            }
        });

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

                        if (fasilitas.getIdLaundry().equals(laundry.getIdLaundry())){
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

    public void reloadData(){
        getDataFasilitas();
    }
}
