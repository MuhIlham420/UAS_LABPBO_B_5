public class Customer extends Akun {

    public Customer(int id, String nama, String password) {
        super(id, nama, password);
    }

    public Pesanan buatPesanan(int idPesanan, Meja meja) {
        System.out.println("Customer " + getNama() + " membuat pesanan baru untuk Meja " + (meja != null ? meja.getNomor() : "N/A") + ".");
        Pesanan p = new Pesanan(idPesanan, meja);
        return p;
    }
}
