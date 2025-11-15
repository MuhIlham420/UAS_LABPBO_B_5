public class Customer extends Akun {

    public Customer(int id, String nama, String password) {
        super(id, nama, password);
    }

    public Pesanan buatPesanan() {
        System.out.println("Customer " + getNama() + " membuat pesanan baru.");
        return null; // Placeholder
    }
}