public class Pegawai extends Akun {
    private String peran;

    public Pegawai(int id, String nama, String password, String peran) {
        super(id, nama, password);
        this.peran = peran;
    }

    public String getPeran() {
        return peran;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

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