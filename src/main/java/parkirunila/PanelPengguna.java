package parkirunila;

import java.util.List;
import javafx.scene.control.Tab;

/**
 * Superclass abstrak untuk tampilan setiap role.
 * Setiap subclass membuat tab menunya sendiri (polimorfisme).
 */
public abstract class PanelPengguna {

    private final Sistem sistem;

    public PanelPengguna(Sistem sistem) {
        this.sistem = sistem;
    }

    protected Sistem getSistem() {
        return sistem;
    }

    /** Menyimpan seluruh data ke file setelah ada perubahan. */
    protected void simpan() {
        Penyimpanan.simpan(sistem);
    }

    /** Membuat daftar tab menu untuk role ini. */
    public abstract List<Tab> buatTab();
}
