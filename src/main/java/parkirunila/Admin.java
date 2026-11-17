package parkirunila;

/**
 * Subclass Pengguna: pengelola sistem parkir.
 */
public class Admin extends Pengguna {

    private static final long serialVersionUID = 1L;

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
