/**
 * Kelas yang merepresentasikan detail dari suatu pesanan menu.
 * Menyimpan informasi tentang item menu, jumlah, dan catatan khusus.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class DetailPesanan {
    private MenuItem item;
    private int jumlah;
    private String catatan;

    /**
     * Konstruktor untuk membuat objek DetailPesanan.
     * 
     * @param item    Item menu yang dipesan
     * @param jumlah  Jumlah item yang dipesan
     * @param catatan Catatan khusus untuk item pesanan
     */
    public DetailPesanan(MenuItem item, int jumlah, String catatan) {
        this.item = item;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    /**
     * Mendapatkan item menu dari detail pesanan.
     * 
     * @return Item menu yang dipesan
     */
    public MenuItem getItem() {
        return item;
    }

    /**
     * Mengatur item menu untuk detail pesanan.
     * 
     * @param item Item menu baru
     */
    public void setItem(MenuItem item) {
        this.item = item;
    }

    /**
     * Mendapatkan jumlah item yang dipesan.
     * 
     * @return Jumlah item pesanan
     */
    public int getJumlah() {
        return jumlah;
    }

    /**
     * Mengatur jumlah item pesanan.
     * 
     * @param jumlah Jumlah item baru
     */
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    /**
     * Mendapatkan catatan khusus untuk pesanan.
     * 
     * @return Catatan pesanan
     */
    public String getCatatan() {
        return catatan;
    }

    /**
     * Mengatur catatan khusus untuk pesanan.
     * 
     * @param catatan Catatan pesanan baru
     */
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    /**
     * Menghitung subtotal untuk detail pesanan ini.
     * 
     * @return Subtotal (harga item × jumlah)
     */
    public double getSubtotal() {
        return item.getHarga() * jumlah;
    }
}