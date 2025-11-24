import java.time.LocalDateTime;

public class Transaksi {
    private int idTransaksi;
    private Pesanan pesanan;
    private Pembayaran metodePembayaran;
    private LocalDateTime timestamp;

    public Transaksi(int idTransaksi, Pesanan pesanan, Pembayaran metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.pesanan = pesanan;
        this.metodePembayaran = metodePembayaran;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public Pembayaran getMetodePembayaran() {
        return metodePembayaran;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void konfirmasi() {
        this.timestamp = LocalDateTime.now();

        double total = pesanan.getTotalHarga();
        System.out.println("Konfirmasi transaksi " + idTransaksi + " untuk pesanan " + pesanan.getIdPesanan());
        System.out.println("Total: Rp" + total);

        metodePembayaran.prosesBayar(total);
    }
}