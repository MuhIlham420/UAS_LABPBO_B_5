/**
 * Kelas abstrak yang merepresentasikan item menu.
 * Menyediakan atribut dasar dan metode untuk semua jenis menu.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public abstract class MenuItem {
    protected int id; 
    protected String nama;
    protected double harga;

    /**
     * Konstruktor untuk membuat objek MenuItem.
     * 
     * @param id    Identifikasi unik item menu
     * @param nama  Nama item menu
     * @param harga Harga item menu
     */
    public MenuItem(int id, String nama, double harga) { 
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    /**
     * Mendapatkan ID item menu.
     * 
     * @return ID item menu
     */
    public int getId() {
        return id;
    }

    /**
     * Mendapatkan nama item menu.
     * 
     * @return Nama item menu
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengatur nama item menu.
     * 
     * @param nama Nama item menu baru
     */
    public void setNama(String nama) {
        this.nama = nama;
    }

    /**
     * Mendapatkan harga item menu.
     * 
     * @return Harga item menu
     */
    public double getHarga() {
        return harga;
    }

    /**
     * Mengatur harga item menu.
     * 
     * @param harga Harga item menu baru
     */
    public void setHarga(double harga) {
        this.harga = harga;
    }

    /**
     * Method abstrak untuk mendapatkan informasi item menu.
     * Harus diimplementasikan oleh subclass.
     * 
     * @return String berisi informasi item menu
     */
    public abstract String getInfo();
}