/**
 * Interface untuk metode pembayaran.
 * Menyediakan method untuk memproses pembayaran.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public interface Pembayaran {
    
    /**
     * Memproses pembayaran dengan jumlah total tertentu.
     * 
     * @param total Jumlah total yang harus dibayar
     */
    void prosesBayar(double total);
}