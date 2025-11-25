import java.time.LocalDateTime;

/**
 * Kelas yang merepresentasikan transaksi pembayaran.
 * Menghubungkan pesanan dengan metode pembayaran dan mencatat waktu transaksi.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class Transaksi {
    private int idTransaksi;
    private Pesanan pesanan;
    private Pembayaran metodePembayaran;
    private LocalDateTime timestamp;

    /**
     * Konstruktor untuk membuat objek Transaksi.
     * 
     * @param idTransaksi       Identifikasi unik transaksi
     * @param pesanan           Objek Pesanan yang akan dibayar
     * @param metodePembayaran  Metode pembayaran yang digunakan
     */
    public Transaksi(int idTransaksi, Pesanan pesanan, Pembayaran metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.pesanan = pesanan;
        this.metodePembayaran = metodePembayaran;
    }

    /**
     * Mendapatkan ID transaksi.
     * 
     * @return ID transaksi
     */
    public int getIdTransaksi() {
        return idTransaksi;
    }

    /**
     * Mendapatkan pesanan yang terkait dengan transaksi.
     * 
     * @return Objek Pesanan
     */
    public Pesanan getPesanan() {
        return pesanan;
    }

    /**
     * Mendapatkan metode pembayaran yang digunakan.
     * 
     * @return Objek Pembayaran
     */
    public Pembayaran getMetodePembayaran() {
        return metodePembayaran;
    }

    /**
     * Mendapatkan timestamp transaksi.
     * 
     * @return Waktu transaksi dilakukan
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Mengkonfirmasi dan memproses transaksi.
     * Method ini akan mencatat waktu transaksi dan memproses pembayaran.
     */
    public void konfirmasi() {
        this.timestamp = LocalDateTime.now();

        double total = pesanan.getTotalHarga();
        System.out.println("Konfirmasi transaksi " + idTransaksi + " untuk pesanan " + pesanan.getIdPesanan());
        System.out.println("Total: Rp" + total);

        metodePembayaran.prosesBayar(total);
    }
}