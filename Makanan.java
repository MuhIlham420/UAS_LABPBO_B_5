/**
 * Kelas yang merepresentasikan menu makanan.
 * Menyimpan informasi khusus makanan seperti tingkat pedas dan kategori.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class Makanan extends MenuItem {
    private int tingkatPedas;
    private String kategori;

    /**
     * Konstruktor untuk membuat objek Makanan.
     * 
     * @param id           Identifikasi unik makanan
     * @param nama         Nama makanan
     * @param harga        Harga makanan
     * @param tingkatPedas Tingkat kepedasan makanan (skala 1-10)
     * @param kategori     Kategori makanan (contoh: appetizer, main course, dessert)
     */
    public Makanan(int id, String nama, double harga, int tingkatPedas, String kategori) { 
        super(id, nama, harga); 
        this.tingkatPedas = tingkatPedas;
        this.kategori = kategori;
    }

    /**
     * Mendapatkan tingkat pedas makanan.
     * 
     * @return Tingkat kepedasan makanan
     */
    public int getTingkatPedas() {
        return tingkatPedas;
    }

    /**
     * Mendapatkan kategori makanan.
     * 
     * @return Kategori makanan
     */
    public String getKategori() {
        return kategori;
    }

    /**
     * Mengatur tingkat pedas makanan.
     * 
     * @param tingkatPedas Tingkat kepedasan baru
     */
    public void setTingkatPedas(int tingkatPedas) { 
        this.tingkatPedas = tingkatPedas; 
    }

    /**
     * Mengatur kategori makanan.
     * 
     * @param kategori Kategori makanan baru
     */
    public void setKategori(String kategori) { 
        this.kategori = kategori; 
    }

    /**
     * Mendapatkan informasi lengkap makanan dalam format string.
     * 
     * @return String berisi informasi makanan yang diformat
     */
    @Override
    public String getInfo() {
        return String.format("%-5d | %-10s | %-20s | Rp%-10.2f | %-15s | %-7d | %-10s | %-10s", 
                             getId(), 
                             "Makanan",
                             getNama(), 
                             getHarga(), 
                             getKategori(),
                             getTingkatPedas(), // Kolom Pedas
                             "-",               // Kolom Ukuran (kosong)
                             "-");              // Kolom Suhu (kosong)
    }
}