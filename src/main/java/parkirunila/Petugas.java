package parkirunila;

/**
 * Subclass Pengguna (opsional): petugas yang mencatat kendaraan tamu.
 */
public class Petugas extends Pengguna {

    private static final long serialVersionUID = 1L;

    public Petugas(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Petugas Parkir";
    }
}
