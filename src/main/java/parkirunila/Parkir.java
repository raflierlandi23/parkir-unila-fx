package parkirunila;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pengelola kapasitas slot dan seluruh catatan parkir.
 */
public class Parkir implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lokasi parkir di lingkungan kampus UNILA (dapat disesuaikan). */
    public static final String[] LOKASI = {
        "Rektorat",
        "Gedung Serba Guna (GSG)",
        "Perpustakaan Pusat",
        "Fakultas Ekonomi dan Bisnis",
        "Fakultas Teknik",
        "FMIPA",
        "Fakultas Hukum",
        "FISIP",
        "FKIP",
        "Fakultas Pertanian",
        "Fakultas Kedokteran"
    };

    private int kapasitasMotor;
    private int kapasitasMobil;
    private final ArrayList<CatatanParkir> semuaCatatan = new ArrayList<>();

    public Parkir(int kapasitasMotor, int kapasitasMobil) {
        this.kapasitasMotor = kapasitasMotor;
        this.kapasitasMobil = kapasitasMobil;
    }

    public void setKapasitas(int motor, int mobil) {
        this.kapasitasMotor = motor;
        this.kapasitasMobil = mobil;
    }

    public int getKapasitas(String jenis) {
        return jenis.equals(Kendaraan.MOTOR) ? kapasitasMotor : kapasitasMobil;
    }

    public int hitungTerisi(String jenis) {
        int jumlah = 0;
        for (CatatanParkir c : semuaCatatan) {
            if (c.masihParkir() && c.getKendaraan().getJenis().equals(jenis)) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public int sisaSlot(String jenis) {
        return getKapasitas(jenis) - hitungTerisi(jenis);
    }

    private CatatanParkir cariAktif(String plat) {
        for (CatatanParkir c : semuaCatatan) {
            if (c.masihParkir() && c.getKendaraan().getPlatNomor().equalsIgnoreCase(plat.trim())) {
                return c;
            }
        }
        return null;
    }

    /** Mengembalikan null jika berhasil, atau pesan error jika gagal. */
    public String masuk(Kendaraan k, String pemilik, String lokasi) {
        if (cariAktif(k.getPlatNomor()) != null) {
            return "Kendaraan " + k.getPlatNomor() + " sudah tercatat sedang parkir.";
        }
        if (sisaSlot(k.getJenis()) <= 0) {
            return "Slot parkir " + k.getJenis() + " penuh!";
        }
        semuaCatatan.add(new CatatanParkir(k, pemilik, lokasi));
        return null;
    }

    /** Mengembalikan catatan yang ditutup, atau null jika kendaraan tidak sedang parkir. */
    public CatatanParkir keluar(String plat) {
        CatatanParkir c = cariAktif(plat);
        if (c != null) {
            c.catatKeluar();
        }
        return c;
    }

    public List<CatatanParkir> getSedangParkir() {
        List<CatatanParkir> hasil = new ArrayList<>();
        for (CatatanParkir c : semuaCatatan) {
            if (c.masihParkir()) {
                hasil.add(c);
            }
        }
        return hasil;
    }

    /** Parameter pemilik bernilai null untuk seluruh riwayat. Data terbaru berada di atas. */
    public List<CatatanParkir> getRiwayat(String pemilik) {
        List<CatatanParkir> hasil = new ArrayList<>();
        for (CatatanParkir c : semuaCatatan) {
            if (pemilik == null || c.getPemilik().equals(pemilik)) {
                hasil.add(0, c);
            }
        }
        return hasil;
    }
}
