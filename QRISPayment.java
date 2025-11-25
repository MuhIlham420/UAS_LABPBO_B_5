/**
 * Kelas yang mengimplementasikan pembayaran menggunakan QRIS.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class QRISPayment implements Pembayaran {

    /**
     * Memproses pembayaran menggunakan QRIS.
     * 
     * @param total Jumlah total yang harus dibayar
     */
    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran QRIS sebesar Rp" + total);
    }
}