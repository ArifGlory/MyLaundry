package myproject.mylaundry.Kelas;

import java.io.Serializable;

public class Fasilitas implements Serializable {
    public String idFasilitas;
    public String namaFasilitas;
    public String fotoFasilitas;
    public String idLaundry;
    public long harga;
    public String idPemilik;

    public Fasilitas(){}

    public Fasilitas(String idFasilitas, String namaFasilitas, String fotoFasilitas, String idLaundry, long harga, String idPemilik) {
        this.idFasilitas = idFasilitas;
        this.namaFasilitas = namaFasilitas;
        this.fotoFasilitas = fotoFasilitas;
        this.idLaundry = idLaundry;
        this.harga = harga;
        this.idPemilik = idPemilik;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }



    public String getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(String idFasilitas) {
        this.idFasilitas = idFasilitas;
    }

    public String getNamaFasilitas() {
        return namaFasilitas;
    }

    public void setNamaFasilitas(String namaFasilitas) {
        this.namaFasilitas = namaFasilitas;
    }

    public String getFotoFasilitas() {
        return fotoFasilitas;
    }

    public void setFotoFasilitas(String fotoFasilitas) {
        this.fotoFasilitas = fotoFasilitas;
    }

    public String getIdLaundry() {
        return idLaundry;
    }

    public void setIdLaundry(String idLaundry) {
        this.idLaundry = idLaundry;
    }
}
