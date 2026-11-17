package parkirunila;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Tampilan untuk role Pengendara: kendaraan saya, parkir masuk/keluar, riwayat, dan profil.
 */
public class PanelPengendara extends PanelPengguna {

    private final Pengendara pengguna;
    private final ObservableList<Kendaraan> dataKendaraan;

    public PanelPengendara(Sistem sistem, Pengendara pengguna) {
        super(sistem);
        this.pengguna = pengguna;
        this.dataKendaraan = FXCollections.observableArrayList(pengguna.getDaftarKendaraan());
    }

    @Override
    public List<Tab> buatTab() {
        return List.of(buatTabKendaraan(), buatTabParkir(), buatTabRiwayat(), buatTabProfil());
    }

    // ----- Tab 1: Kendaraan saya -----
    private Tab buatTabKendaraan() {
        ComboBox<String> jenis = new ComboBox<>(
                FXCollections.observableArrayList(Kendaraan.MOTOR, Kendaraan.MOBIL));
        jenis.setValue(Kendaraan.MOTOR);
        TextField plat = new TextField();
        plat.setPromptText("Contoh: BE 1234 AB");
        Button tambah = new Button("Daftarkan");

        GridPane form = UI.form();
        form.addRow(0, new Label("Jenis"), jenis);
        form.addRow(1, new Label("Plat nomor"), plat);
        form.add(tambah, 1, 2);

        TableView<Kendaraan> tabel = UI.tabel();
        UI.kolom(tabel, "Jenis", 120, Kendaraan::getJenis);
        UI.kolom(tabel, "Plat Nomor", 160, Kendaraan::getPlatNomor);
        UI.kolom(tabel, "Area", 220, k -> "Parkir " + k.getJenis());
        tabel.setItems(dataKendaraan);

        // Event handling: tombol Daftarkan
        tambah.setOnAction(e -> {
            String nomor = plat.getText().trim();
            if (nomor.isEmpty()) {
                UI.peringatan("Plat nomor tidak boleh kosong.");
            } else if (getSistem().platTerdaftar(nomor)) {
                UI.peringatan("Plat nomor " + nomor.toUpperCase() + " sudah terdaftar.");
            } else {
                Kendaraan k = jenis.getValue().equals(Kendaraan.MOTOR) ? new Motor(nomor) : new Mobil(nomor);
                pengguna.getDaftarKendaraan().add(k);
                dataKendaraan.add(k);
                plat.clear();
                simpan();
                UI.info("Kendaraan berhasil didaftarkan.");
            }
        });

        return UI.tab("Kendaraan Saya",
                UI.kotak(UI.judul("Daftarkan kendaraan"), form, UI.judul("Kendaraan terdaftar"), tabel),
                () -> { });
    }

    // ----- Tab 2: Parkir masuk / keluar -----
    private Tab buatTabParkir() {
        PanelSlot slot = new PanelSlot(getSistem().getParkir());
        ComboBox<Kendaraan> pilihKendaraan = new ComboBox<>(dataKendaraan);
        pilihKendaraan.setPromptText("Pilih kendaraan");
        pilihKendaraan.setPrefWidth(300);
        ComboBox<String> pilihLokasi = new ComboBox<>(FXCollections.observableArrayList(Parkir.LOKASI));
        pilihLokasi.setPromptText("Pilih lokasi parkir");
        pilihLokasi.setPrefWidth(300);
        Button masuk = new Button("Parkir Masuk");
        Button keluar = new Button("Parkir Keluar");

        masuk.setOnAction(e -> {
            Kendaraan k = pilihKendaraan.getValue();
            String lokasi = pilihLokasi.getValue();
            if (k == null) {
                UI.peringatan("Pilih kendaraan terlebih dahulu. Daftarkan di tab Kendaraan Saya jika belum ada.");
            } else if (lokasi == null) {
                UI.peringatan("Pilih lokasi parkir terlebih dahulu.");
            } else {
                String error = getSistem().getParkir().masuk(k, pengguna.getUsername(), lokasi);
                if (error == null) {
                    simpan();
                    UI.info("Parkir masuk tercatat di " + lokasi + ".");
                } else {
                    UI.peringatan(error);
                }
                slot.refresh();
            }
        });

        keluar.setOnAction(e -> {
            Kendaraan k = pilihKendaraan.getValue();
            if (k == null) {
                UI.peringatan("Pilih kendaraan terlebih dahulu.");
                return;
            }
            CatatanParkir c = getSistem().getParkir().keluar(k.getPlatNomor());
            if (c == null) {
                UI.peringatan("Kendaraan ini tidak sedang parkir.");
            } else {
                simpan();
                UI.info("Parkir keluar tercatat.\nDurasi parkir: " + c.getDurasi() + " (gratis)");
            }
            slot.refresh();
        });

        GridPane form = UI.form();
        form.addRow(0, new Label("Kendaraan"), pilihKendaraan);
        form.addRow(1, new Label("Lokasi"), pilihLokasi);
        form.add(new HBox(10, masuk, keluar), 1, 2);

        return UI.tab("Parkir",
                UI.kotak(UI.judul("Parkir masuk / keluar"), slot, form),
                slot::refresh);
    }

    // ----- Tab 3: Riwayat saya -----
    private Tab buatTabRiwayat() {
        TableView<CatatanParkir> tabel = UI.tabelCatatan();
        return UI.tab("Riwayat Saya",
                UI.kotak(UI.judul("Riwayat parkir saya"), tabel),
                () -> tabel.setItems(FXCollections.observableArrayList(
                        getSistem().getParkir().getRiwayat(pengguna.getUsername()))));
    }

    // ----- Tab 4: Profil (memakai setter) -----
    private Tab buatTabProfil() {
        Label info = new Label();
        TextField namaBaru = new TextField();
        namaBaru.setPromptText("Kosongkan jika tidak diubah");
        PasswordField passwordLama = new PasswordField();
        PasswordField passwordBaru = new PasswordField();
        Button simpan = new Button("Simpan perubahan");

        GridPane form = UI.form();
        form.addRow(0, new Label("Nama baru"), namaBaru);
        form.addRow(1, new Label("Password lama"), passwordLama);
        form.addRow(2, new Label("Password baru"), passwordBaru);
        form.add(simpan, 1, 3);

        simpan.setOnAction(e -> {
            boolean gantiNama = !namaBaru.getText().trim().isEmpty();
            boolean gantiPassword = !passwordLama.getText().isEmpty() || !passwordBaru.getText().isEmpty();

            if (!gantiNama && !gantiPassword) {
                UI.peringatan("Tidak ada data yang diubah. Isi nama baru atau password.");
                return;
            }
            if (gantiPassword) {
                if (!pengguna.cekPassword(passwordLama.getText())) {
                    UI.peringatan("Password lama salah.");
                    return;
                }
                if (passwordBaru.getText().isEmpty()) {
                    UI.peringatan("Password baru tidak boleh kosong.");
                    return;
                }
                pengguna.setPassword(passwordBaru.getText());
            }
            if (gantiNama) {
                pengguna.setNama(namaBaru.getText());
            }
            namaBaru.clear();
            passwordLama.clear();
            passwordBaru.clear();
            info.setText("Nama: " + pengguna.getNama() + "  |  " + pengguna.getStatus()
                    + " (" + pengguna.getNomorInduk() + ")");
            simpan();
            UI.info("Profil berhasil diperbarui.");
        });

        return UI.tab("Profil",
                UI.kotak(UI.judul("Profil saya"), info, form),
                () -> info.setText("Nama: " + pengguna.getNama() + "  |  " + pengguna.getStatus()
                        + " (" + pengguna.getNomorInduk() + ")"));
    }
}
