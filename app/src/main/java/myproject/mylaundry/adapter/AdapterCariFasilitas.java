package myproject.mylaundry.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.mylaundry.Kelas.Fasilitas;
import myproject.mylaundry.Kelas.SharedVariable;
import myproject.mylaundry.R;
import myproject.mylaundry.activity.DetailLaundryActivity;
import myproject.mylaundry.activity.FasilitasActivity;
import myproject.mylaundry.activity.ResultCariFasilitasActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterCariFasilitas extends RecyclerView.Adapter<AdapterCariFasilitas.MyViewHolder> {

    private Context mContext;
    private List<Fasilitas> fasilitasList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaFasilitas;
        ImageView ivFasilitas;
        public RelativeLayout lineFasilitas;

        public MyViewHolder(View view) {
            super(view);
            tvNamaFasilitas = (TextView) view.findViewById(R.id.tvNamaFasilitas);
            ivFasilitas = (ImageView) view.findViewById(R.id.ivFasilitas);
            lineFasilitas = view.findViewById(R.id.lineFasilitas);

        }
    }

    public AdapterCariFasilitas(Context mContext, List<Fasilitas> fasilitasList) {
        this.mContext = mContext;
        this.fasilitasList = fasilitasList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("fasilitas");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cari_fasilitas, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (fasilitasList.isEmpty()){

            Log.d("isiFasilitas: ",""+fasilitasList.size());
        }else {

            final Fasilitas fasilitas = fasilitasList.get(position);

            holder.tvNamaFasilitas.setText(fasilitas.getNamaFasilitas());

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            Double myHarga = Double.valueOf(fasilitas.getHarga());

            Glide.with(mContext)
                    .load(fasilitas.getFotoFasilitas())
                    .into(holder.ivFasilitas);

            holder.lineFasilitas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String keyword = fasilitas.namaFasilitas.toLowerCase();

                    Intent intent = new Intent(mContext, ResultCariFasilitasActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("keyword",keyword);
                    mContext.startActivity(intent);
                }
            });


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return fasilitasList.size();
    }
}
