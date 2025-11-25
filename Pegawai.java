/**
 * Kelas yang merepresentasikan akun Pegawai dalam sistem.
 * Pegawai dapat memiliki peran berbeda dan dapat mengupdate status pesanan.
 * 
 * @author Kelompok 5 - UAS LAB PBO B
 * @version 1.0
 */
public class Pegawai extends Akun {
    private String peran;

    /**
     * Konstruktor untuk membuat objek Pegawai.
     * 
     * @param id       Identifikasi unik pegawai
     * @param nama     Nama pegawai
     * @param password Kata sandi pegawai
     * @param peran    Peran pegawai (Koki, Pelayan, Kasir, dll.)
     */
    public Pegawai(int id, String nama, String password, String peran) {
        super(id, nama, password);
        this.peran = peran;
    }

    /**
     * Mendapatkan peran pegawai.
     * 
     * @return Peran pegawai
     */
    public String getPeran() {
        return peran;
    }

    /**
     * Mengatur peran pegawai.
     * 
     * @param peran Peran pegawai baru
     */
    public void setPeran(String peran) {
        this.peran = peran;
    }

    /**
     * Mengupdate status pesanan berdasarkan peran pegawai.
     * Koki hanya dapat mengubah status menjadi 'Selesai Dimasak',
     * sedangkan Pelayan tidak dapat mengubah status menjadi 'Selesai Dimasak'.
     * 
     * @param pesanan Objek Pesanan yang statusnya akan diupdate
     * @param status  Status baru untuk pesanan
     */
    public void updateStatusPesanan(Pesanan pesanan, String status) {
        if (this.peran.equals("Koki") && !status.equals("Selesai Dimasak")) {
            System.out.println("Koki hanya dapat mengubah status menjadi 'Selesai Dimasak'. Aksi dibatalkan.");
            return;
        }

        if (this.peran.equals("Pelayan") && status.equals("Selesai Dimasak")) {
            System.out.println(
                    "Pelayan tidak bisa mengubah status menjadi 'Selesai Dimasak'. Itu tugas Koki. Aksi dibatalkan.");
            return;
        }

        System.out.println("Pegawai " + getNama() + " (" + this.peran + ") memperbarui status pesanan "
                + pesanan.getIdPesanan() + " dari '" + pesanan.getStatus() + "' menjadi '" + status + "'.");
        pesanan.setStatus(status);
    }
}