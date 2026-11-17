package parkirunila;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Tampilan untuk role Admin: pengguna, kendaraan sedang parkir, riwayat, slot dan kapasitas.
 */
public class PanelAdmin extends PanelPengguna {

    public PanelAdmin(Sistem sistem) {
        super(sistem);
    }

    @Override
    public List<Tab> buatTab() {
        return List.of(buatTabPengguna(), buatTabSedangParkir(), buatTabRiwayat(), buatTabKapasitas());
    }

    // ----- Tab 1: Pengguna -----
    private Tab buatTabPengguna() {
        TableView<Pengguna> tabel = UI.tabel();
        UI.kolom(tabel, "Username", 140, Pengguna::getUsername);
        UI.kolom(tabel, "Role", 120, Pengguna::getRole);
        UI.kolom(tabel, "Keterangan", 380, Pengguna::getKeterangan);
        Button hapus = new Button("Hapus pengguna terpilih");

        Runnable muat = () -> tabel.setItems(
                FXCollections.observableArrayList(getSistem().getDaftarPengguna()));

        hapus.setOnAction(e -> {
            Pengguna p = tabel.getSelectionModel().getSelectedItem();
            if (p == null) {
                UI.peringatan("Pilih pengguna di tabel terlebih dahulu.");
            } else if (p instanceof Admin) {
                UI.peringatan("Akun admin tidak dapat dihapus.");
            } else if (UI.konfirmasi("Hapus pengguna " + p.getUsername() + "?")) {
                getSistem().getDaftarPengguna().remove(p);
                simpan();
                muat.run();
            }
        });

        return UI.tab("Pengguna",
                UI.kotak(UI.judul("Daftar pengguna"), tabel, hapus), muat);
    }

    // ----- Tab 2: Sedang parkir -----
    private Tab buatTabSedangParkir() {
        TableView<CatatanParkir> tabel = UI.tabelCatatan();
        return UI.tab("Sedang Parkir",
                UI.kotak(UI.judul("Kendaraan yang sedang parkir"), tabel),
                () -> tabel.setItems(FXCollections.observableArrayList(
                        getSistem().getParkir().getSedangParkir())));
    }

    // ----- Tab 3: Riwayat -----
    private Tab buatTabRiwayat() {
        TableView<CatatanParkir> tabel = UI.tabelCatatan();
        return UI.tab("Riwayat",
                UI.kotak(UI.judul("Seluruh riwayat parkir"), tabel),
                () -> tabel.setItems(FXCollections.observableArrayList(
                        getSistem().getParkir().getRiwayat(null))));
    }

    // ----- Tab 4: Slot dan kapasitas -----
    private Tab buatTabKapasitas() {
        Parkir parkir = getSistem().getParkir();
        PanelSlot slot = new PanelSlot(parkir);
        TextField kapMotor = new TextField();
        TextField kapMobil = new TextField();
        Button simpan = new Button("Simpan kapasitas");

        GridPane form = UI.form();
        form.addRow(0, new Label("Kapasitas Motor"), kapMotor);
        form.addRow(1, new Label("Kapasitas Mobil"), kapMobil);
        form.add(simpan, 1, 2);

        simpan.setOnAction(e -> {
            int motor;
            int mobil;
            // try-catch: menangani input yang bukan angka
            try {
                motor = Integer.parseInt(kapMotor.getText().trim());
                mobil = Integer.parseInt(kapMobil.getText().trim());
            } catch (NumberFormatException ex) {
                UI.peringatan("Kapasitas harus berupa angka bulat.");
                return;
            }
            if (motor < 0 || mobil < 0) {
                UI.peringatan("Kapasitas tidak boleh negatif.");
            } else if (motor < parkir.hitungTerisi(Kendaraan.MOTOR)
                    || mobil < parkir.hitungTerisi(Kendaraan.MOBIL)) {
                UI.peringatan("Kapasitas tidak boleh lebih kecil dari jumlah kendaraan yang sedang parkir.");
            } else {
                parkir.setKapasitas(motor, mobil);
                simpan();
                slot.refresh();
                UI.info("Kapasitas diperbarui.");
            }
        });

        return UI.tab("Slot & Kapasitas",
                UI.kotak(UI.judul("Ketersediaan slot"), slot, UI.judul("Ubah kapasitas"), form),
                () -> {
                    slot.refresh();
                    kapMotor.setText(String.valueOf(parkir.getKapasitas(Kendaraan.MOTOR)));
                    kapMobil.setText(String.valueOf(parkir.getKapasitas(Kendaraan.MOBIL)));
                });
    }
}
