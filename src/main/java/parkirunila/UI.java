package parkirunila;

import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Kumpulan fungsi bantu untuk membuat komponen JavaFX yang dipakai berulang.
 */
public class UI {

    private UI() {
        // kelas utilitas, tidak untuk diinstansiasi
    }

    // ----- Pop-up Alert -----

    public static void info(String teks) {
        tampilkan(Alert.AlertType.INFORMATION, "Informasi", teks);
    }

    /** Alert peringatan, dipakai untuk validasi input (kolom kosong, format salah, dan sebagainya). */
    public static void peringatan(String teks) {
        tampilkan(Alert.AlertType.WARNING, "Peringatan", teks);
    }

    private static void tampilkan(Alert.AlertType tipe, String judul, String teks) {
        Alert alert = new Alert(tipe, teks, ButtonType.OK);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static boolean konfirmasi(String teks) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, teks, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // ----- Layout dan komponen -----

    public static Label judul(String teks) {
        Label label = new Label(teks);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        return label;
    }

    /** GridPane dengan jarak antar kolom dan baris, dipakai untuk form input. */
    public static GridPane form() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    /** VBox dengan padding, dipakai sebagai isi setiap tab. */
    public static VBox kotak(Node... isi) {
        VBox box = new VBox(12, isi);
        box.setPadding(new Insets(15));
        return box;
    }

    public static <S> TableView<S> tabel() {
        TableView<S> tabel = new TableView<>();
        tabel.setPlaceholder(new Label("Belum ada data."));
        VBox.setVgrow(tabel, Priority.ALWAYS);
        return tabel;
    }

    public static <S> void kolom(TableView<S> tabel, String judul, double lebar, Function<S, String> isi) {
        TableColumn<S, String> kolom = new TableColumn<>(judul);
        kolom.setPrefWidth(lebar);
        kolom.setCellValueFactory(c -> new SimpleStringProperty(isi.apply(c.getValue())));
        tabel.getColumns().add(kolom);
    }

    public static TableView<CatatanParkir> tabelCatatan() {
        TableView<CatatanParkir> tabel = tabel();
        kolom(tabel, "Plat Nomor", 105, c -> c.getKendaraan().getPlatNomor());
        kolom(tabel, "Jenis", 70, c -> c.getKendaraan().getJenis());
        kolom(tabel, "Pemilik", 90, CatatanParkir::getPemilik);
        kolom(tabel, "Lokasi", 170, CatatanParkir::getLokasi);
        kolom(tabel, "Masuk", 160, CatatanParkir::getTeksMasuk);
        kolom(tabel, "Keluar", 160, CatatanParkir::getTeksKeluar);
        kolom(tabel, "Durasi", 170, CatatanParkir::getDurasi);
        return tabel;
    }

    /** Membuat tab. Isinya dimuat ulang setiap kali tab dibuka. */
    public static Tab tab(String judul, Node isi, Runnable saatDibuka) {
        Tab tab = new Tab(judul, isi);
        tab.setClosable(false);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                saatDibuka.run();
            }
        });
        saatDibuka.run();
        return tab;
    }
}
