import java.util.ArrayList;
import java.util.List;

/**
 * Kelas yang merepresentasikan pesanan dalam sistem restoran.
 * Menyimpan informasi tentang pesanan termasuk item yang dipesan, meja, dan customer.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class Pesanan {
    private int idPesanan;
    private String status;
    private List<DetailPesanan> daftarItem;
    private Meja meja;
    private Customer customer;

    /**
     * Konstruktor untuk membuat objek Pesanan tanpa customer.
     * 
     * @param idPesanan Identifikasi unik pesanan
     * @param meja      Meja yang dipesan
     */
    public Pesanan(int idPesanan, Meja meja) {
        this.idPesanan = idPesanan;
        this.meja = meja;
        this.status = "Dipesan";
        this.daftarItem = new ArrayList<>();
        this.customer = null;
    }

    /**
     * Konstruktor untuk membuat objek Pesanan dengan customer.
     * 
     * @param idPesanan Identifikasi unik pesanan
     * @param meja      Meja yang dipesan
     * @param customer  Customer yang membuat pesanan
     */
    public Pesanan(int idPesanan, Meja meja, Customer customer) {
        this.idPesanan = idPesanan;
        this.meja = meja;
        this.customer = customer;
        this.status = "Dipesan";
        this.daftarItem = new ArrayList<>();
    }

    /**
     * Mendapatkan ID pesanan.
     * 
     * @return ID pesanan
     */
    public int getIdPesanan() {
        return idPesanan;
    }

    /**
     * Mengatur ID pesanan.
     * 
     * @param idPesanan ID pesanan baru
     */
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }

    /**
     * Mendapatkan status pesanan.
     * 
     * @return Status pesanan saat ini
     */
    public String getStatus() {
        return status;
    }

    /**
     * Mengatur status pesanan.
     * 
     * @param status Status pesanan baru
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Mendapatkan daftar item pesanan.
     * 
     * @return List berisi detail pesanan
     */
    public List<DetailPesanan> getDaftarItem() {
        return daftarItem;
    }

    /**
     * Mendapatkan meja yang dipesan.
     * 
     * @return Objek Meja yang dipesan
     */
    public Meja getMeja() {
        return meja;
    }

    /**
     * Mengatur meja untuk pesanan.
     * 
     * @param meja Meja baru untuk pesanan
     */
    public void setMeja(Meja meja) {
        this.meja = meja;
    }

    /**
     * Menambahkan detail pesanan ke dalam daftar item.
     * 
     * @param detail Objek DetailPesanan yang akan ditambahkan
     */
    public void tambahDetailPesanan(DetailPesanan detail) {
        this.daftarItem.add(detail);
    }

    /**
     * Menghitung total harga dari semua item pesanan.
     * 
     * @return Total harga pesanan
     */
    public double getTotalHarga() {
        double total = 0;
        for (DetailPesanan detail : daftarItem) {
            total += detail.getSubtotal();
        }
        return total;
    }

    /**
     * Mendapatkan customer yang membuat pesanan.
     * 
     * @return Objek Customer pembuat pesanan
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Mengatur customer untuk pesanan.
     * 
     * @param customer Customer baru untuk pesanan
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}