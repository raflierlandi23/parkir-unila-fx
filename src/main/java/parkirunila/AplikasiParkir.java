package parkirunila;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Aplikasi JavaFX Sistem Manajemen Parkir Kampus UNILA.
 * Menampilkan halaman login/registrasi, lalu dasbor sesuai role pengguna.
 */
public class AplikasiParkir extends Application {

    private Sistem sistem;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Muat data dari file (Deserialization), atau mulai dengan data baru
        sistem = Penyimpanan.muat();
        if (sistem == null) {
            sistem = new Sistem();
        }

        stage.setTitle("Sistem Manajemen Parkir Kampus UNILA");
        stage.setMinWidth(780);
        stage.setMinHeight(540);
        stage.setOnCloseRequest(e -> Penyimpanan.simpan(sistem)); // simpan saat jendela ditutup

        tampilkanLogin();
        stage.show();
    }

    // =====================================================
    // TAHAP 1: halaman login dan registrasi (Label, TextField, Button, GridPane, VBox)
    // =====================================================
    void tampilkanLogin() {
        Label judul = new Label("SISTEM MANAJEMEN PARKIR KAMPUS");
        judul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0b4f9c;");
        Label sub = new Label("Universitas Lampung (UNILA)  |  Smart Mobility");
        PanelSlot slot = new PanelSlot(sistem.getParkir());

        TabPane tabs = new TabPane(buatTabLogin(), buatTabRegistrasi());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setMaxWidth(480);

        VBox root = new VBox(14, judul, sub, slot, tabs);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        stage.setScene(new Scene(root, 900, 640));
    }

    private Tab buatTabLogin() {
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button btnLogin = new Button("Login");

        GridPane form = UI.form();
        form.addRow(0, new Label("Username"), username);
        form.addRow(1, new Label("Password"), password);
        form.add(btnLogin, 1, 2);

        Label contoh = new Label("Akun contoh: admin / admin123,  petugas / petugas123");
        contoh.setStyle("-fx-text-fill: #667085;");

        // TAHAP 2 dan 3: event handling dengan validasi kolom kosong
        Runnable prosesLogin = () -> {
            if (username.getText().trim().isEmpty() || password.getText().isEmpty()) {
                UI.peringatan("Username dan password wajib diisi.");
                return;
            }
            Pengguna pengguna = sistem.login(username.getText().trim(), password.getText());
            if (pengguna == null) {
                UI.peringatan("Username atau password salah.");
            } else {
                tampilkanDashboard(pengguna);
            }
        };
        btnLogin.setOnAction(e -> prosesLogin.run());
        password.setOnAction(e -> prosesLogin.run()); // tekan Enter di kolom password

        Tab tab = new Tab("Login", UI.kotak(form, contoh));
        tab.setClosable(false);
        return tab;
    }

    private Tab buatTabRegistrasi() {
        TextField nama = new TextField();
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList(Pengendara.STATUS));
        status.setValue(Pengendara.STATUS[0]);
        Label labelInduk = new Label("NPM");
        status.valueProperty().addListener((obs, lama, baru) ->
                labelInduk.setText("Mahasiswa".equals(baru) ? "NPM" : "NIP"));
        TextField nomorInduk = new TextField();
        TextField usernameBaru = new TextField();
        PasswordField passwordBaru = new PasswordField();
        Button btnDaftar = new Button("Daftar");

        GridPane form = UI.form();
        form.addRow(0, new Label("Nama lengkap"), nama);
        form.addRow(1, new Label("Status"), status);
        form.addRow(2, labelInduk, nomorInduk);
        form.addRow(3, new Label("Username"), usernameBaru);
        form.addRow(4, new Label("Password"), passwordBaru);
        form.add(btnDaftar, 1, 5);

        btnDaftar.setOnAction(e -> {
            String username = usernameBaru.getText().trim();
            if (nama.getText().trim().isEmpty() || nomorInduk.getText().trim().isEmpty()
                    || username.isEmpty() || passwordBaru.getText().isEmpty()) {
                UI.peringatan("Semua kolom wajib diisi.");
            } else if (!nomorInduk.getText().trim().matches("\\d+")) {
                UI.peringatan(labelInduk.getText() + " harus berupa angka.");
            } else if (sistem.cariPengguna(username) != null) {
                UI.peringatan("Username sudah dipakai.");
            } else {
                sistem.getDaftarPengguna().add(new Pengendara(username, passwordBaru.getText(),
                        nama.getText().trim(), status.getValue(), nomorInduk.getText().trim()));
                Penyimpanan.simpan(sistem);
                nama.clear();
                nomorInduk.clear();
                usernameBaru.clear();
                passwordBaru.clear();
                UI.info("Registrasi berhasil. Silakan login.");
            }
        });

        Tab tab = new Tab("Registrasi Pengendara", UI.kotak(form));
        tab.setClosable(false);
        return tab;
    }

    // =====================================================
    // Dasbor sesuai role (polimorfisme lewat PanelPengguna)
    // =====================================================
    void tampilkanDashboard(Pengguna pengguna) {
        Label judul = new Label("Parkir Kampus UNILA");
        judul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label info = new Label(pengguna.getUsername() + " (" + pengguna.getRole() + ")");
        info.setStyle("-fx-text-fill: white;");
        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            Penyimpanan.simpan(sistem);
            tampilkanLogin();
        });

        Region pengisi = new Region();
        HBox.setHgrow(pengisi, Priority.ALWAYS);
        HBox atas = new HBox(12, judul, pengisi, info, logout);
        atas.setAlignment(Pos.CENTER_LEFT);
        atas.setPadding(new Insets(10, 15, 10, 15));
        atas.setStyle("-fx-background-color: #0b4f9c;");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(buatPanel(pengguna).buatTab());

        BorderPane root = new BorderPane();
        root.setTop(atas);
        root.setCenter(tabs);
        stage.setScene(new Scene(root, 960, 620));
    }

    private PanelPengguna buatPanel(Pengguna pengguna) {
        if (pengguna instanceof Pengendara) {
            return new PanelPengendara(sistem, (Pengendara) pengguna);
        }
        if (pengguna instanceof Admin) {
            return new PanelAdmin(sistem);
        }
        return new PanelPetugas(sistem);
    }
}
