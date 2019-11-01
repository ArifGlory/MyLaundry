package myproject.mylaundry.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.activity.ResultActivity;
import myproject.mylaundry.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomeUser extends BaseFragment {


    public FragmentHomeUser() {
        // Required empty public constructor
    }

    RelativeLayout rlHarga,rlAlamat,rlFasilitas;
    public static android.app.AlertDialog dialog;
    private long maxHarga;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        rlHarga = view.findViewById(R.id.rlHarga);
        rlAlamat = view.findViewById(R.id.rlAlamat);
        rlFasilitas = view.findViewById(R.id.rlFasilitas);

        rlAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater minlfater = LayoutInflater.from(getActivity());
                View v2 = minlfater.inflate(R.layout.dialog_alamat, null);
                dialog = new android.app.AlertDialog.Builder(getActivity()).create();
                dialog.setView(v2);

                final EditText etAlamat = v2.findViewById(R.id.etAlamat);
                Button btnCari = v2.findViewById(R.id.btnCari);

                btnCari.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etAlamat.getText().toString().equals("") || etAlamat.getText().toString() == null){
                            Toast.makeText(getActivity(),"Anda belum menulis alamat",Toast.LENGTH_SHORT).show();
                        }else {
                            SharedVariable.keyword = etAlamat.getText().toString();
                            Intent intent = new Intent(getActivity(),ResultActivity.class);
                            intent.putExtra("tipe","alamat");
                            startActivity(intent);

                        }
                    }
                });


                dialog.show();
            }
        });
        rlHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater minlfater = LayoutInflater.from(getActivity());
                View v2 = minlfater.inflate(R.layout.dialog_harga, null);
                dialog = new android.app.AlertDialog.Builder(getActivity()).create();
                dialog.setView(v2);

                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
                Locale localeID = new Locale("in", "ID");
                final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                final TextView txtHarga = v2.findViewById(R.id.txtHarga);
                Button btnCari = v2.findViewById(R.id.btnCari);

                SeekBar seekBar = v2.findViewById(R.id.seekBar);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxHarga = progress;
                        txtHarga.setText("Rp. 0 - "+ formatRupiah.format((double) progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                btnCari.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (maxHarga == 0){
                            Toast.makeText(getActivity(),"Anda belum memilih rentang harga",Toast.LENGTH_SHORT).show();
                        }else {
                            SharedVariable.maxHarga = maxHarga;
                            Intent intent = new Intent(getActivity(),ResultActivity.class);
                            intent.putExtra("tipe","harga");
                            startActivity(intent);

                        }
                    }
                });


                dialog.show();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
