/**
 * Kelas yang merepresentasikan akun Customer dalam sistem.
 * Customer dapat membuat pesanan untuk meja tertentu.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class Customer extends Akun {

    /**
     * Konstruktor untuk membuat objek Customer dengan parameter yang ditentukan.
     * 
     * @param id       Identifikasi unik untuk customer
     * @param nama     Nama customer
     * @param password Kata sandi untuk mengakses akun customer
     */
    public Customer(int id, String nama, String password) {
        super(id, nama, password);
    }

    /**
     * Membuat pesanan baru untuk meja tertentu.
     * Method ini akan membuat objek Pesanan dan mengaitkannya dengan customer ini.
     * 
     * @param idPesanan Identifikasi unik untuk pesanan
     * @param meja      Objek Meja yang dipesan oleh customer
     * @return Objek Pesanan yang baru dibuat
     */
    public Pesanan buatPesanan(int idPesanan, Meja meja) {
        System.out.println("Customer " + getNama() + " membuat pesanan baru untuk Meja " + (meja != null ? meja.getNomor() : "N/A") + ".");
        Pesanan p = new Pesanan(idPesanan, meja, this);
        return p;
    }
}