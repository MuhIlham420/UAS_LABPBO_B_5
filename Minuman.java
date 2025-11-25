/**
 * Kelas yang merepresentasikan menu minuman.
 * Menyimpan informasi khusus minuman seperti ukuran, suhu, dan kategori.
 * 
 * @author [Nama Anda]
 * @version 1.0
 */
public class Minuman extends MenuItem {
    private String ukuran; 
    private String suhu;
    private String kategori; 

    /**
     * Konstruktor untuk membuat objek Minuman.
     * 
     * @param id       Identifikasi unik minuman
     * @param nama     Nama minuman
     * @param harga    Harga minuman
     * @param ukuran   Ukuran minuman (small, medium, large)
     * @param suhu     Suhu minuman (dingin, panas, hangat)
     * @param kategori Kategori minuman (soft drink, juice, coffee, dll.)
     */
    public Minuman(int id, String nama, double harga, String ukuran, String suhu, String kategori) { 
        super(id, nama, harga); 
        this.ukuran = ukuran;
        this.suhu = suhu;
        this.kategori = kategori; 
    }

    /**
     * Mendapatkan ukuran minuman.
     * 
     * @return Ukuran minuman
     */
    public String getUkuran() {
        return ukuran;
    }

    /**
     * Mendapatkan suhu minuman.
     * 
     * @return Suhu minuman
     */
    public String getSuhu() {
        return suhu;
    }

    /**
     * Mendapatkan kategori minuman.
     * 
     * @return Kategori minuman
     */
    public String getKategori() {
        return kategori;
    }

    /**
     * Mengatur ukuran minuman.
     * 
     * @param ukuran Ukuran minuman baru
     */
    public void setUkuran(String ukuran) { 
        this.ukuran = ukuran; 
    }

    /**
     * Mengatur suhu minuman.
     * 
     * @param suhu Suhu minuman baru
     */
    public void setSuhu(String suhu) { 
        this.suhu = suhu; 
    }

    /**
     * Mendapatkan informasi lengkap minuman dalam format string.
     * 
     * @return String berisi informasi minuman yang diformat
     */
    @Override
    public String getInfo() {
        return String.format("%-5d | %-10s | %-20s | Rp%-10.2f | %-15s | %-7s | %-10s | %-10s", 
                             getId(), 
                             "Minuman",
                             getNama(), 
                             getHarga(), 
                             getKategori(),
                             "-",         // Kolom Pedas (kosong)
                             getUkuran(), // Kolom Ukuran
                             getSuhu());  // Kolom Suhu
    }
}