package parkirunila;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Panel ringkasan ketersediaan slot parkir motor dan mobil.
 */
public final class PanelSlot extends HBox {

    private final Parkir parkir;
    private final Label motor = new Label();
    private final Label mobil = new Label();

    public PanelSlot(Parkir parkir) {
        super(30);
        this.parkir = parkir;
        setPadding(new Insets(10, 15, 10, 15));
        setStyle("-fx-background-color: #e8f1fb; -fx-background-radius: 6;");
        motor.setStyle("-fx-font-weight: bold;");
        mobil.setStyle("-fx-font-weight: bold;");
        getChildren().addAll(new Label("Ketersediaan slot:"), motor, mobil);
        refresh();
    }

    public void refresh() {
        motor.setText("Motor: " + parkir.sisaSlot(Kendaraan.MOTOR) + " tersedia dari "
                + parkir.getKapasitas(Kendaraan.MOTOR));
        mobil.setText("Mobil: " + parkir.sisaSlot(Kendaraan.MOBIL) + " tersedia dari "
                + parkir.getKapasitas(Kendaraan.MOBIL));
    }
}
