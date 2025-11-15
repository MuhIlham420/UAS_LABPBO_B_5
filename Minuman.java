
public class Minuman extends MenuItem {
    private String ukuran; 
    private String suhu;
    private String kategori; 

    public Minuman(int id, String nama, double harga, String ukuran, String suhu, String kategori) { 
        super(id, nama, harga); 
        this.ukuran = ukuran;
        this.suhu = suhu;
        this.kategori = kategori; 
    }

    public String getUkuran() {
        return ukuran;
    }
    public String getSuhu() {
        return suhu;
    }
    public String getKategori() {
        return kategori;
    }
    
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }
    public void setSuhu(String suhu) { this.suhu = suhu; }

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