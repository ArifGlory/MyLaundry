package myproject.mylaundry.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.Laundry;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.activity.BerandaPemilikActivity;
import myproject.mylaundry.activity.DetailLaundryActivity;
import myproject.mylaundry.activity.UbahDataLaundry;
import myproject.mylaundry.fragment.FragmentHomePemilik;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterLaundry extends RecyclerView.Adapter<AdapterLaundry.MyViewHolder> {

    private Context mContext;
    private List<Laundry> laundryList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaLaundry,tvAlamat,tvHarga;
        ImageView ivLaundry;
        public LinearLayout lineLaundry;

        public MyViewHolder(View view) {
            super(view);
            tvNamaLaundry = (TextView) view.findViewById(R.id.tvNamaLaundry);
            ivLaundry = (ImageView) view.findViewById(R.id.ivLaundry);
            tvAlamat = (TextView) view.findViewById(R.id.tvAlamat);
            tvHarga = (TextView) view.findViewById(R.id.tvHarga);
            lineLaundry = view.findViewById(R.id.lineLaundry);

        }
    }

    public AdapterLaundry(Context mContext, List<Laundry> laundryList) {
        this.mContext = mContext;
        this.laundryList = laundryList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("laundry");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laundry, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (laundryList.isEmpty()){

            Log.d("isiLaundry: ",""+laundryList.size());
        }else {

            final Laundry laundry = laundryList.get(position);

            holder.tvNamaLaundry.setText(laundry.getNamaLaundry());
            holder.tvAlamat.setText(laundry.getAlamat());

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            Double myHarga = Double.valueOf(laundry.getHargaPerKg());
            holder.tvHarga.setText("Harga per Kg : "+formatRupiah.format((double) myHarga));

            Glide.with(mContext)
                    .load(laundry.getFoto())
                    .into(holder.ivLaundry);

            holder.lineLaundry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailLaundryActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("laundry",laundry);
                    mContext.startActivity(intent);
                }
            });
            holder.lineLaundry.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (laundry.getIdPemilik().equals(SharedVariable.userID)){
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Kelola Laundry")
                                .setContentText("Anda dapat melakukan perubahan data laundry ini")
                                .setConfirmText("Ubah Data")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(mContext, UbahDataLaundry.class);
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("laundry",laundry);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .setCancelButton("Hapus", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        pDialogLoading.show();
                                        ref.document(laundry.getIdLaundry()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pDialogLoading.dismiss();
                                                if (mContext instanceof BerandaPemilikActivity){
                                                    ((BerandaPemilikActivity)mContext).reloadListLaundry();
                                                }
                                            }
                                        });

                                    }
                                })
                                .show();
                    }

                    return true;
                }
            });


        }

    }



    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return laundryList.size();
    }
}
