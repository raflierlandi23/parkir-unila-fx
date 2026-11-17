package parkirunila;

import java.io.Serializable;

/**
 * Superclass abstrak untuk semua pengguna sistem.
 * Subclass: Pengendara, Admin, Petugas.
 */
public abstract class Pengguna implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private String password;

    public Pengguna(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean cekPassword(String pw) {
        return password.equals(pw);
    }

    public void setPassword(String passwordBaru) {
        if (passwordBaru != null && !passwordBaru.isEmpty()) {
            this.password = passwordBaru;
        }
    }

    public abstract String getRole();

    /** Keterangan tambahan pengguna untuk tabel admin. */
    public String getKeterangan() {
        return "-";
    }
}
