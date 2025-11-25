/**
 * Kelas yang merepresentasikan meja di restoran.
 * Menyimpan informasi nomor meja dan status ketersediaannya.
 * 
 * @author [Nama Anda]
 * @version 1.0
 */
public class Meja {
    private int nomor;
    private String status;

    /**
     * Konstruktor untuk membuat objek Meja.
     * 
     * @param nomor  Nomor identifikasi meja
     * @param status Status meja (tersedia, terisi, reserved, dll.)
     */
    public Meja(int nomor, String status) {
        this.nomor = nomor;
        this.status = status;
    }

    /**
     * Mendapatkan nomor meja.
     * 
     * @return Nomor meja
     */
    public int getNomor() {
        return nomor;
    }

    /**
     * Mengatur nomor meja.
     * 
     * @param nomor Nomor meja baru
     */
    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    /**
     * Mendapatkan status meja.
     * 
     * @return Status meja saat ini
     */
    public String getStatus() {
        return status;
    }

    /**
     * Mengatur status meja.
     * 
     * @param status Status meja baru
     */
    public void setStatus(String status) {
        this.status = status;
    }
}