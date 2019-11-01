package myproject.mylaundry.Kelas;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Laundry implements Serializable {
    public String namaLaundry;
    public long hargaPerKg;
    public String alamat;
    public String lat;
    public String lon;
    public String foto;
    public String idLaundry;
    public String idPemilik;

    public Laundry(String namaLaundry, long hargaPerKg, String alamat, String lat, String lon, String foto,String idPemilik) {
        this.namaLaundry = namaLaundry;
        this.hargaPerKg = hargaPerKg;
        this.alamat = alamat;
        this.lat = lat;
        this.lon = lon;
        this.foto = foto;
        this.idPemilik = idPemilik;
    }

    public Laundry(){

    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }

    public String getNamaLaundry() {
        return namaLaundry;
    }

    public void setNamaLaundry(String namaLaundry) {
        this.namaLaundry = namaLaundry;
    }

    public long getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(long hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdLaundry() {
        return idLaundry;
    }

    public void setIdLaundry(String idLaundry) {
        this.idLaundry = idLaundry;
    }

}
