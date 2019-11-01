package myproject.mylaundry.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.MainActivity;
import myproject.mylaundry.R;
import myproject.mylaundry.activity.AddLaundryActivity;
import myproject.mylaundry.adapter.AdapterLaundry;
import myproject.mylaundry.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomePemilik extends BaseFragment implements Callback {


    public FragmentHomePemilik() {
        // Required empty public constructor
    }


    FloatingActionButton btnCreate;
    RecyclerView rvMyLaundry;
    AdapterLaundry adapter;
    List<Laundry> laundryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home_pemilik, container, false);
        btnCreate = view.findViewById(R.id.btnCreate);
        rvMyLaundry = view.findViewById(R.id.rvMyLaundry);

        ref = firestore.collection("laundry");

        laundryList = new ArrayList<>();
        adapter = new AdapterLaundry(getActivity(),laundryList);

        rvMyLaundry.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyLaundry.setHasFixedSize(true);
        rvMyLaundry.setItemAnimator(new DefaultItemAnimator());
        rvMyLaundry.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLaundryActivity.class);
                startActivity(intent);
            }
        });

        getDataMyLaundry(SharedVariable.userID,adapter,laundryList);


        return view;
    }


    public void reloadData(){
        getDataMyLaundry(SharedVariable.userID,adapter,laundryList);
    }



    @Override
    public void onResume() {
        super.onResume();
        getDataMyLaundry(SharedVariable.userID,adapter,laundryList);
    }

    private class Interface {
    }
}
