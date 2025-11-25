/**
 * Kelas yang mengimplementasikan pembayaran menggunakan kartu (debit/kredit).
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class CardPayment implements Pembayaran {

    /**
     * Memproses pembayaran menggunakan kartu.
     * 
     * @param total Jumlah total yang harus dibayar
     */
    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran kartu (Card) sebesar Rp" + total);
    }
}