public class Makanan extends MenuItem {
    private int tingkatPedas;
    private String kategori;

    public Makanan(int id, String nama, double harga, int tingkatPedas, String kategori) { 
        super(id, nama, harga); 
        this.tingkatPedas = tingkatPedas;
        this.kategori = kategori;
    }

    public int getTingkatPedas() {
        return tingkatPedas;
    }
    public String getKategori() {
        return kategori;
    }
    
    public void setTingkatPedas(int tingkatPedas) { this.tingkatPedas = tingkatPedas; }
    public void setKategori(String kategori) { this.kategori = kategori; }

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