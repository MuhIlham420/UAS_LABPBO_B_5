public class CashPayment implements Pembayaran {

    @Override
    public void prosesBayar(double total) {
        System.out.println("Memproses pembayaran tunai (Cash) sebesar Rp" + total);
    }
}