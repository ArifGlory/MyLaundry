package myproject.mylaundry.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import myproject.mylaundry.MainActivity;
import myproject.mylaundry.R;
import myproject.mylaundry.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends BaseFragment {


    public FragmentLogin() {
        // Required empty public constructor
    }


    Button btnKeLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnKeLogin = view.findViewById(R.id.btnKeLogin);

        btnKeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
