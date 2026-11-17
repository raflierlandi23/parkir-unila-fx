package parkirunila;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Satu catatan parkir: kendaraan, pemilik, lokasi, jam masuk dan jam keluar.
 */
public class CatatanParkir implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Kendaraan kendaraan;
    private final String pemilik;
    private final String lokasi;
    private final LocalDateTime jamMasuk;
    private LocalDateTime jamKeluar;

    public CatatanParkir(Kendaraan kendaraan, String pemilik, String lokasi) {
        this.kendaraan = kendaraan;
        this.pemilik = pemilik;
        this.lokasi = lokasi;
        this.jamMasuk = LocalDateTime.now();
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public String getPemilik() {
        return pemilik;
    }

    public String getLokasi() {
        return lokasi;
    }

    public boolean masihParkir() {
        return jamKeluar == null;
    }

    public void catatKeluar() {
        this.jamKeluar = LocalDateTime.now();
    }

    public String getTeksMasuk() {
        return jamMasuk.format(FORMAT);
    }

    public String getTeksKeluar() {
        return masihParkir() ? "-" : jamKeluar.format(FORMAT);
    }

    public String getDurasi() {
        LocalDateTime akhir = masihParkir() ? LocalDateTime.now() : jamKeluar;
        Duration d = Duration.between(jamMasuk, akhir);
        return d.toHours() + " jam " + (d.toMinutes() % 60) + " menit "
                + (d.getSeconds() % 60) + " detik";
    }
}
