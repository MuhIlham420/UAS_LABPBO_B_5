/**
 * Kelas yang mengimplementasikan pembayaran secara tunai (cash).
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class CashPayment implements Pembayaran {

    /**
     * Memproses pembayaran secara tunai.
     * 
     * @param total Jumlah total yang harus dibayar
     */
    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran tunai (Cash) sebesar Rp" + total);
    }
}