public class QRISPayment implements Pembayaran {

    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran QRIS sebesar Rp" + total);
    }
}