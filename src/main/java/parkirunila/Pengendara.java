package parkirunila;

import java.util.ArrayList;
import java.util.List;

/**
 * Subclass Pengguna: mahasiswa, dosen, atau tenaga kependidikan pemilik kendaraan.
 */
public class Pengendara extends Pengguna {

    private static final long serialVersionUID = 1L;

    public static final String[] STATUS = {"Mahasiswa", "Dosen", "Tenaga Kependidikan"};

    private String nama;
    private final String status;
    private final String nomorInduk;
    private final ArrayList<Kendaraan> daftarKendaraan = new ArrayList<>();

    public Pengendara(String username, String password, String nama, String status, String nomorInduk) {
        super(username, password);
        this.nama = nama;
        this.status = status;
        this.nomorInduk = nomorInduk;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama.trim();
        }
    }

    public String getStatus() {
        return status;
    }

    public String getNomorInduk() {
        return nomorInduk;
    }

    public List<Kendaraan> getDaftarKendaraan() {
        return daftarKendaraan;
    }

    @Override
    public String getRole() {
        return "Pengendara";
    }

    @Override
    public String getKeterangan() {
        return nama + " - " + status + " (" + nomorInduk + ")";
    }
}
