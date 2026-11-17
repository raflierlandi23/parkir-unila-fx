package parkirunila;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Tampilan untuk role Petugas Parkir: mencatat kendaraan tamu masuk dan keluar.
 */
public class PanelPetugas extends PanelPengguna {

    public PanelPetugas(Sistem sistem) {
        super(sistem);
    }

    @Override
    public List<Tab> buatTab() {
        Parkir parkir = getSistem().getParkir();
        PanelSlot slot = new PanelSlot(parkir);
        TableView<CatatanParkir> tabel = UI.tabelCatatan();

        Runnable muat = () -> {
            slot.refresh();
            tabel.setItems(FXCollections.observableArrayList(parkir.getSedangParkir()));
        };

        // Form kendaraan masuk (tamu)
        ComboBox<String> jenis = new ComboBox<>(
                FXCollections.observableArrayList(Kendaraan.MOTOR, Kendaraan.MOBIL));
        jenis.setValue(Kendaraan.MOTOR);
        TextField platMasuk = new TextField();
        platMasuk.setPromptText("Contoh: BE 1234 AB");
        ComboBox<String> lokasi = new ComboBox<>(FXCollections.observableArrayList(Parkir.LOKASI));
        lokasi.setPromptText("Pilih lokasi parkir");
        Button catatMasuk = new Button("Catat Masuk");

        GridPane formMasuk = UI.form();
        formMasuk.addRow(0, new Label("Jenis"), jenis);
        formMasuk.addRow(1, new Label("Plat nomor"), platMasuk);
        formMasuk.addRow(2, new Label("Lokasi"), lokasi);
        formMasuk.add(catatMasuk, 1, 3);

        catatMasuk.setOnAction(e -> {
            String nomor = platMasuk.getText().trim();
            if (nomor.isEmpty()) {
                UI.peringatan("Plat nomor tidak boleh kosong.");
            } else if (lokasi.getValue() == null) {
                UI.peringatan("Pilih lokasi parkir terlebih dahulu.");
            } else {
                Kendaraan k = jenis.getValue().equals(Kendaraan.MOTOR) ? new Motor(nomor) : new Mobil(nomor);
                String error = parkir.masuk(k, "Tamu", lokasi.getValue());
                if (error == null) {
                    platMasuk.clear();
                    simpan();
                    UI.info("Kendaraan masuk tercatat di " + lokasi.getValue() + ".");
                } else {
                    UI.peringatan(error);
                }
                muat.run();
            }
        });

        // Form kendaraan keluar
        TextField platKeluar = new TextField();
        platKeluar.setPromptText("Plat nomor kendaraan keluar");
        Button catatKeluar = new Button("Catat Keluar");

        catatKeluar.setOnAction(e -> {
            if (platKeluar.getText().trim().isEmpty()) {
                UI.peringatan("Plat nomor tidak boleh kosong.");
                return;
            }
            CatatanParkir c = parkir.keluar(platKeluar.getText());
            if (c == null) {
                UI.peringatan("Kendaraan tidak ditemukan atau tidak sedang parkir.");
            } else {
                platKeluar.clear();
                simpan();
                UI.info("Kendaraan keluar tercatat. Durasi: " + c.getDurasi());
            }
            muat.run();
        });

        HBox barisKeluar = new HBox(10, new Label("Plat nomor"), platKeluar, catatKeluar);
        barisKeluar.setAlignment(Pos.CENTER_LEFT);

        Tab tab = UI.tab("Catat Masuk / Keluar",
                UI.kotak(slot,
                        UI.judul("Kendaraan masuk (tamu)"), formMasuk,
                        UI.judul("Kendaraan keluar"), barisKeluar,
                        UI.judul("Sedang parkir"), tabel),
                muat);

        return List.of(tab);
    }
}
