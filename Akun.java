/**
 * Kelas abstrak yang merepresentasikan akun pengguna dalam sistem.
 * Menyediakan atribut dasar dan metode yang umum untuk semua jenis akun.
 * 
 * @author [Kelompok 5 - UAS LAB PBO B]
 * @version 1.0
 */
public abstract class Akun {
    protected int id;
    protected String nama;
    protected String password;

    /**
     * Konstruktor untuk membuat objek Akun dengan parameter yang ditentukan.
     * 
     * @param id       Identifikasi unik untuk akun
     * @param nama     Nama pengguna akun
     * @param password Kata sandi untuk mengakses akun
     */
    public Akun(int id, String nama, String password) {
        this.id = id;
        this.nama = nama;
        this.password = password;
    }

    /**
     * Mendapatkan ID akun.
     * 
     * @return ID akun saat ini
     */
    public int getId() {
        return id;
    }

    /**
     * Mengatur ID akun.
     * 
     * @param id ID baru untuk akun
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Mendapatkan nama pengguna akun.
     * 
     * @return Nama pengguna akun saat ini
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengatur nama pengguna akun.
     * 
     * @param nama Nama baru untuk akun
     */
    public void setNama(String nama) {
        this.nama = nama;
    }

    /**
     * Mendapatkan kata sandi akun.
     * 
     * @return Kata sandi akun saat ini
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mengatur kata sandi akun.
     * 
     * @param password Kata sandi baru untuk akun
     */
    public void setPassword(String password) {
        this.password = password;
    }
}